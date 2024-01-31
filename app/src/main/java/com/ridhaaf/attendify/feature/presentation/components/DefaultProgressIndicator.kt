package com.ridhaaf.attendify.feature.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun DefaultProgressIndicator() {
    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center),
        color = MaterialTheme.colorScheme.primary,
    )
}