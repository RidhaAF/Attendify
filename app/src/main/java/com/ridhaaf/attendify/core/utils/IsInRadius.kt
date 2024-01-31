package com.ridhaaf.attendify.core.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng

fun isInRadius(employeeLocation: Location): Boolean {
    val officeLoc = LatLng(OfficeLocation.LATITUDE, OfficeLocation.LONGITUDE)
    val employeeLoc = LatLng(employeeLocation.latitude, employeeLocation.longitude)

    val locationA = Location("Office")
    locationA.latitude = officeLoc.latitude
    locationA.longitude = officeLoc.longitude
    val locationB = Location("Employee")
    locationB.latitude = employeeLoc.latitude
    locationB.longitude = employeeLoc.longitude

    var distance: Float = locationA.distanceTo(locationB)
    distance /= 1000

    return distance <= 0.1
}