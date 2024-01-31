package com.ridhaaf.attendify.feature.domain.repositories.attendance

import android.content.Context
import android.location.Location
import android.net.Uri
import com.google.android.gms.location.FusedLocationProviderClient
import com.ridhaaf.attendify.core.utils.Resource
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

    fun getEmployeeLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
    ): Flow<Resource<Location>>
}