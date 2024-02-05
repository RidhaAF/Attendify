package com.ridhaaf.attendify.core.utils

import android.icu.text.SimpleDateFormat
import java.util.Locale

fun timeFormatter(time: Long): String {
    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return if (time == 0L) {
        "00:00:00"
    } else {
        dateFormat.format(time)
    }
}