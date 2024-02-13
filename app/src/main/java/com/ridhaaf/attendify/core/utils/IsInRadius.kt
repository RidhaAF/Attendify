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

    val result = distanceInKm <= 0.1 // 100 m

    println("employeeLocation: ${employeeLocation.latitude}, ${employeeLocation.longitude}")
    println("officeLocation: ${officeLocation.latitude}, ${officeLocation.longitude}")
    println("distance: $distance m")
    println("distanceInKm: $distanceInKm km")
    println("isInRadius: $result\n")

    return result
}