package com.ridhaaf.attendify.core.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun timeFormatter(time: Long): String {
    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return if (time == 0L) {
        "00:00:00"
    } else {
        dateFormat.format(time)
    }
}

fun dateFormatter(time: Long): String {
    val format = "EE, d MMMM yyyy"
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.format(time)
}