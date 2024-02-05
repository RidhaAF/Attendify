package com.ridhaaf.attendify.feature.data.repositories.attendance

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.ridhaaf.attendify.core.utils.Resource
import com.ridhaaf.attendify.feature.data.models.attendance.Attendance
import com.ridhaaf.attendify.feature.domain.repositories.attendance.AttendanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
) : AttendanceRepository {
    override fun clockIn(
        context: Context,
        data: Map<String, Any>,
        photo: Uri,
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val photoUrl = uploadAttendancePhoto(context, photo)

            val userId = auth.currentUser?.uid ?: ""
            val docId = attendancesCollection().document().id

            val attendance = Attendance(
                userId = userId,
                clockInDateTime = data["dateTime"] as Long,
                clockInLatitude = data["latitude"] as Double,
                clockInLongitude = data["longitude"] as Double,
                clockInPhotoUrl = photoUrl,
                createdAt = System.currentTimeMillis(),
            )

            val batch = firestore.batch()
            val attendanceRef = attendancesCollection().document(docId)
            batch.set(attendanceRef, attendance)
            batch.update(userStatusReference(userId), "status", true)

            batch.commit().await()

            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Oops, something went wrong!"))
        }
    }

    override fun clockOut(
        context: Context,
        data: Map<String, Any>,
        photo: Uri,
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val userId = auth.currentUser?.uid ?: ""

            val docId = getLatestClockInDocumentId(userId)

            if (docId != null) {
                val photoUrl = uploadAttendancePhoto(context, photo)

                val documentRef = attendancesCollection().document(docId)

                val attendance = Attendance(
                    userId = userId,
                    clockOutDateTime = data["dateTime"] as Long,
                    clockOutLatitude = data["latitude"] as Double,
                    clockOutLongitude = data["longitude"] as Double,
                    clockOutPhotoUrl = photoUrl,
                )

                firestore.runBatch { batch ->
                    batch.update(
                        documentRef,
                        "clockOutDateTime", attendance.clockOutDateTime,
                        "clockOutLatitude", attendance.clockOutLatitude,
                        "clockOutLongitude", attendance.clockOutLongitude,
                        "clockOutPhotoUrl", attendance.clockOutPhotoUrl,
                    )
                }.await()

                updateUserStatus(userId, false)
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("You haven't clocked in yet!"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Oops, something went wrong!"))
        }
    }

    override fun getLatestAttendanceByUserId(): Flow<Resource<Attendance>> = flow {
        emit(Resource.Loading())

        try {
            val userId = auth.currentUser?.uid ?: ""

            val querySnapshot = getLatestClockInDocumentId(userId)?.let {
                attendancesCollection().document(it).get().await()
            }

            if (querySnapshot != null) {
                val attendance = querySnapshot.toObject(Attendance::class.java)
                emit(Resource.Success(attendance!!))
            } else {
                emit(Resource.Error("You haven't clocked in yet!"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Oops, something went wrong!"))
        }
    }

    override fun getEmployeeLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
    ): Flow<Resource<Location>> = flow {
        emit(Resource.Loading())

        try {
            val result = fusedLocationProviderClient.lastLocation.await()

            if (result != null) {
                emit(Resource.Success(result))
            } else {
                emit(Resource.Error("Failed to get location"))
            }
        } catch (e: SecurityException) {
            emit(Resource.Error(e.localizedMessage ?: "Oops, something went wrong!"))
        }
    }

    private suspend fun uploadAttendancePhoto(context: Context, photo: Uri): String {
        return withContext(Dispatchers.IO) {
            try {
                val storagePath = "attendance"

                val filename = "${UUID.randomUUID()}.jpg"

                // Convert the Uri to a Bitmap
                val photoBitmap =
                    BitmapFactory.decodeStream(context.contentResolver.openInputStream(photo))
                        ?: throw NullPointerException("Failed to take photo, please try again")

                // Convert the Bitmap to bytes
                val baos = ByteArrayOutputStream()
                photoBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
                val data = baos.toByteArray()

                // Close the ByteArrayOutputStream
                baos.close()

                val userId = auth.currentUser?.uid

                // Upload the photo to Firebase Storage
                val storageRef = storage.reference.child("$storagePath/$userId/$filename")
                storageRef.putBytes(data).await()

                return@withContext storageRef.downloadUrl.await().toString()
            } catch (e: Exception) {
                throw Exception(e.localizedMessage ?: "Upload photo failed, please try again later")
            }
        }
    }

    private suspend fun getLatestClockInDocumentId(userId: String): String? {
        try {
            val querySnapshot = attendancesCollection().whereEqualTo("userId", userId).orderBy(
                "clockInDateTime", Query.Direction.DESCENDING
            ).limit(1).get().await()

            if (!querySnapshot.isEmpty) {
                return querySnapshot.documents[0].id
            }
        } catch (e: Exception) {
            throw Exception(e.localizedMessage ?: "Get latest clock in document ID failed")
        }

        return null
    }

    private suspend fun updateUserStatus(userId: String, status: Boolean) {
        try {
            firestore.collection("users").document(userId).update("status", status).await()
        } catch (e: Exception) {
            throw Exception(e.localizedMessage ?: "Update user status failed")
        }
    }

    private fun attendancesCollection(): CollectionReference {
        return firestore.collection("attendances")
    }

    private fun userStatusReference(userId: String): DocumentReference {
        return firestore.collection("users").document(userId)
    }
}