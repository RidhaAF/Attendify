package com.ridhaaf.attendify.feature.presentation.location

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.ridhaaf.attendify.core.utils.Resource
import com.ridhaaf.attendify.feature.domain.usecases.attendance.AttendanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val useCase: AttendanceUseCase,
) : ViewModel() {
    private val _state = mutableStateOf(LocationState())
    val state: State<LocationState> = _state

    private fun getEmployeeLocation(fusedLocationProviderClient: FusedLocationProviderClient) {
        viewModelScope.launch {
            useCase.getEmployeeLocation(fusedLocationProviderClient).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = LocationState(
                            isLoading = true,
                        )
                    }

                    is Resource.Success -> {
                        _state.value = LocationState(
                            isLoading = false,
                            location = result.data,
                        )
                    }

                    is Resource.Error -> {
                        _state.value = LocationState(
                            isLoading = false,
                            location = null,
                            error = result.message ?: "Oops, something went wrong!",
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: LocationEvent) {
        when (event) {
            is LocationEvent.GetEmployeeLocation -> {
                getEmployeeLocation(event.fusedLocationProviderClient)
            }
        }
    }
}