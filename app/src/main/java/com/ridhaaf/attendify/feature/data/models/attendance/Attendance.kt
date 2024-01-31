package com.ridhaaf.attendify.feature.data.models.attendance

data class Attendance(
    val userId: String = "",
    val clockInDateTime: Long = 0L,
    val clockInLatitude: Double = 0.0,
    val clockInLongitude: Double = 0.0,
    val clockInPhotoUrl: String? = null,
    val clockOutDateTime: Long = 0L,
    val clockOutLatitude: Double = 0.0,
    val clockOutLongitude: Double = 0.0,
    val clockOutPhotoUrl: String? = null,
    var createdAt: Long = 0L,
)
