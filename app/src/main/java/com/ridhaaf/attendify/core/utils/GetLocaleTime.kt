package com.ridhaaf.attendify.core.utils

fun getLocaleTime(): Long {
    val currentTime = System.currentTimeMillis()
    val gmt = 7 * 60 * 60 * 1000
    return currentTime - gmt
}