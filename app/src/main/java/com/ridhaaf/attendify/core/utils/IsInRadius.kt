package com.ridhaaf.attendify.core.utils

import android.location.Location

fun isInRadius(
    employeeLocation: Location,
    officeLocation: Location = Location("Office").apply {
        latitude = OfficeLocation.LATITUDE
        longitude = OfficeLocation.LONGITUDE
    },
): Boolean {
    if (employeeLocation.latitude == 0.0 && employeeLocation.longitude == 0.0) {
        return false
    }
    val distance: Float = employeeLocation.distanceTo(officeLocation)
    val distanceInKm = distance / 1000

    return distanceInKm <= 0.1 // 100 m
}