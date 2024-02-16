package com.ridhaaf.attendify.feature.domain.repositories.attendance

import android.content.Context
import android.location.Location
import android.net.Uri
import com.google.android.gms.location.FusedLocationProviderClient
import com.ridhaaf.attendify.core.utils.Resource
import com.ridhaaf.attendify.core.utils.SortOption
import com.ridhaaf.attendify.feature.data.models.attendance.Attendance
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    fun clockIn(
        context: Context,
        data: Map<String, Any>,
        photo: Uri,
    ): Flow<Resource<Boolean>>

    fun clockOut(
        context: Context,
        data: Map<String, Any>,
        photo: Uri,
    ): Flow<Resource<Boolean>>

    fun getAttendancesByUserId(sort: SortOption): Flow<Resource<List<Attendance>>>

    fun getLatestAttendanceByUserId(): Flow<Resource<Attendance>>

    fun getEmployeeLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
    ): Flow<Resource<Location>>
}