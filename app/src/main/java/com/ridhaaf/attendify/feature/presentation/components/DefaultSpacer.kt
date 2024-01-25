package com.ridhaaf.attendify.feature.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DefaultSpacer(
    horizontal: Boolean = false,
    size: Int = 16,
) {
    if (horizontal) Spacer(modifier = Modifier.width(size.dp))
    else Spacer(modifier = Modifier.height(size.dp))
}