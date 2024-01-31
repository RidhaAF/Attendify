package com.ridhaaf.attendify.feature.presentation.camera

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ridhaaf.attendify.core.utils.Resource
import com.ridhaaf.attendify.feature.domain.usecases.attendance.AttendanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val useCase: AttendanceUseCase,
) : ViewModel() {
    private val _state = mutableStateOf(CameraState())
    val state: State<CameraState> = _state

    private fun clockIn(context: Context, data: Map<String, Any>, photo: Uri) {
        viewModelScope.launch {
            useCase.clockIn(context, data, photo).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = CameraState(
                            isClockInLoading = true,
                        )
                    }

                    is Resource.Success -> {
                        _state.value = CameraState(
                            isClockInLoading = false,
                            clockIn = result.data ?: false,
                        )
                    }

                    is Resource.Error -> {
                        _state.value = CameraState(
                            isClockInLoading = false,
                            clockIn = false,
                            clockInError = result.message ?: "Oops, something went wrong!",
                        )
                    }
                }
            }
        }
    }

    private fun clockOut(context: Context, data: Map<String, Any>, photo: Uri) {
        viewModelScope.launch {
            useCase.clockOut(context, data, photo).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = CameraState(
                            isClockOutLoading = true,
                        )
                    }

                    is Resource.Success -> {
                        _state.value = CameraState(
                            isClockOutLoading = false,
                            clockOut = result.data ?: false,
                        )
                    }

                    is Resource.Error -> {
                        _state.value = CameraState(
                            isClockOutLoading = false,
                            clockOut = false,
                            clockOutError = result.message ?: "Oops, something went wrong!",
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: CameraEvent) {
        when (event) {
            is CameraEvent.ClockIn -> {
                clockIn(event.context, event.data, event.photo)
            }

            is CameraEvent.ClockOut -> {
                clockOut(event.context, event.data, event.photo)
            }
        }
    }
}