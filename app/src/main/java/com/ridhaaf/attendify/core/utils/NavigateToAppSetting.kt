package com.ridhaaf.attendify.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun Context.navigateToAppSetting(
    location: String = Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
) {
    val intent = Intent(location)
    if (location == Settings.ACTION_APPLICATION_DETAILS_SETTINGS) {
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
    }
    startActivity(intent)
}