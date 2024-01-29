package com.ridhaaf.attendify.feature.presentation.location

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor() : ViewModel() {
    private val _state = mutableStateOf(LocationState())
    val state: State<LocationState> = _state

    private fun getEmployeeLocation(fusedLocationProviderClient: FusedLocationProviderClient) {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _state.value = LocationState(
                        location = task.result,
                    )
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
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