package com.ridhaaf.attendify.core.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun getCurrentDate(): String {
    val currentTime = System.currentTimeMillis()
    val format = "EE, d MMMM yyyy"
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.format(currentTime)
}