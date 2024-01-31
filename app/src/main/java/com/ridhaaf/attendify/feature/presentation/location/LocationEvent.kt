package com.ridhaaf.attendify.feature.presentation.location

import com.google.android.gms.location.FusedLocationProviderClient

sealed class LocationEvent {
    data class GetEmployeeLocation(val fusedLocationProviderClient: FusedLocationProviderClient) : LocationEvent()
}