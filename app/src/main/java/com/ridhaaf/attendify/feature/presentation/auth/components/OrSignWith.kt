package com.ridhaaf.attendify.feature.presentation.auth.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OrSignWith(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Divider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .padding(end = 16.dp),
        )
        Text(text)
        Divider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .padding(start = 16.dp),
        )
    }
}