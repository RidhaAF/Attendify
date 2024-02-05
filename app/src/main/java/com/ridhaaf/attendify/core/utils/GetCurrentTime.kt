package com.ridhaaf.attendify.core.utils

import android.icu.text.SimpleDateFormat
import java.util.Locale

fun getCurrentTime(): String {
    val currentTime = System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return dateFormat.format(currentTime)
}