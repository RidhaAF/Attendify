package com.ridhaaf.attendify.feature.presentation.camera

data class CameraState(
    val isClockInLoading: Boolean = false,
    val clockIn: Boolean = false,
    val clockInError: String = "",
    val isClockOutLoading: Boolean = false,
    val clockOut: Boolean = false,
    val clockOutError: String = "",
)