package com.ridhaaf.attendify.feature.presentation.profile

import android.content.Context
import android.net.Uri

sealed class ProfileEvent {
    data object Refresh : ProfileEvent()
    data class UploadPhoto(val context: Context, val photo: Uri) : ProfileEvent()
    data object DeletePhoto : ProfileEvent()
}