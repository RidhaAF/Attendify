package com.ridhaaf.attendify.feature.presentation.components

import android.content.Context
import android.widget.Toast

fun defaultToast(
    context: Context,
    text: String,
    duration: Int = Toast.LENGTH_SHORT,
) {
    Toast.makeText(context, text, duration).show()
}