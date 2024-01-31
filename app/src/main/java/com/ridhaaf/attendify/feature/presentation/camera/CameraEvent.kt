package com.ridhaaf.attendify.feature.presentation.camera

import android.content.Context
import android.net.Uri

sealed class CameraEvent {
    data class ClockIn(
        val context: Context,
        val data: Map<String, Any>,
        val photo: Uri,
    ) : CameraEvent()

    data class ClockOut(
        val context: Context,
        val data: Map<String, Any>,
        val photo: Uri,
    ) : CameraEvent()
}