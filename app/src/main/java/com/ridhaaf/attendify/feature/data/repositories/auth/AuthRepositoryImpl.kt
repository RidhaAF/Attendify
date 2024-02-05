package com.ridhaaf.attendify.feature.data.repositories.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.ridhaaf.attendify.core.utils.Resource
import com.ridhaaf.attendify.feature.data.models.auth.User
import com.ridhaaf.attendify.feature.domain.repositories.auth.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthRepository {
    override fun isAuthenticated(): Flow<Resource<Boolean>> = flow {
        try {
            emit(Resource.Loading())

            val result = auth.currentUser != null

            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Oops, something went wrong!"))
        }
    }

    override fun signUp(
        name: String,
        email: String,
        password: String,
    ): Flow<Resource<AuthResult>> = flow {
        try {
            emit(Resource.Loading())

            val result = auth.createUserWithEmailAndPassword(email, password).await()

            val user = result.user
            insertUser(
                user?.uid ?: "",
                name,
                email,
            )

            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Oops, something went wrong!"))
        }
    }

    override fun signIn(
        email: String,
        password: String,
    ): Flow<Resource<AuthResult>> = flow {
        try {
            emit(Resource.Loading())

            val result = auth.signInWithEmailAndPassword(email, password).await()

            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Oops, something went wrong!"))
        }
    }

    override fun signInWithGoogle(credential: AuthCredential): Flow<Resource<AuthResult>> = flow {
        try {
            emit(Resource.Loading())

            val result = auth.signInWithCredential(credential).await()
            val isNewUser = result.additionalUserInfo?.isNewUser == true

            val user = result.user
            if (isNewUser) {
                insertUser(
                    user?.uid ?: "",
                    user?.displayName ?: "",
                    user?.email ?: "",
                    user?.photoUrl.toString(),
                )
            }

            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Oops, something went wrong!"))
        }
    }

    override fun signOut(): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())

            val result = auth.signOut()

            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Oops, something went wrong!"))
        }
    }

    override fun getCurrentUser(): Flow<Resource<User>> = flow {
        try {
            emit(Resource.Loading())

            val result = auth.currentUser
            if (result != null) {
                val isSignedInWithGoogle =
                    result.providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID }

                val id = result.uid
                val document = usersCollection().document(id).get().await()

                val user = User()
                if (isSignedInWithGoogle) {
                    user.id = id
                    user.displayName = result.displayName ?: ""
                    user.email = result.email ?: ""
                    user.photoUrl = result.photoUrl.toString()
                    user.status = document["status"] as Boolean
                    user.createdAt = document["createdAt"] as Long
                } else {
                    user.id = id
                    user.displayName = document["displayName"].toString()
                    user.email = document["email"].toString()
                    user.photoUrl = document["photoUrl"].toString()
                    user.status = document["status"] as Boolean
                    user.createdAt = document["createdAt"] as Long
                }
                emit(Resource.Success(user))
            } else {
                emit(Resource.Error("User not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Oops, something went wrong!"))
        }
    }

    private suspend fun insertUser(
        id: String,
        name: String,
        email: String,
        photoUrl: String? = null,
    ) {
        val status = false
        val now = System.currentTimeMillis()
        val user = hashMapOf(
            "displayName" to name,
            "email" to email,
            "photoUrl" to photoUrl,
            "status" to status,
            "createdAt" to now,
        )
        usersCollection().document(id).set(user).await()
    }

    private fun usersCollection(): CollectionReference {
        return firestore.collection("users")
    }
}