package com.ridhaaf.attendify.core.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun getCurrentTime(): String {
    val currentTime = System.currentTimeMillis()
    val format = "HH:mm:ss"
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.format(currentTime)
}