package com.ridhaaf.attendify.feature.domain.usecases.attendance

import android.content.Context
import android.net.Uri
import com.ridhaaf.attendify.core.utils.Resource
import com.ridhaaf.attendify.feature.domain.repositories.attendance.AttendanceRepository
import kotlinx.coroutines.flow.Flow

class AttendanceUseCase(
    private val repository: AttendanceRepository,
) {
    fun clockIn(context: Context, data: Map<String, Any>, photo: Uri): Flow<Resource<Boolean>> {
        return repository.clockIn(context, data, photo)
    }

    fun clockOut(context: Context, data: Map<String, Any>, photo: Uri): Flow<Resource<Boolean>> {
        return repository.clockOut(context, data, photo)
    }
}