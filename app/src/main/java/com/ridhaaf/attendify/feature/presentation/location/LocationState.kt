package com.ridhaaf.attendify.feature.presentation.location

import android.location.Location

data class LocationState(
    val isLoading: Boolean = false,
    val location: Location? = null,
    val error: String = "",
)
