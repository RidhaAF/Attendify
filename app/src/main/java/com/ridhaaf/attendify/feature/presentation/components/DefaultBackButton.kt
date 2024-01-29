package com.ridhaaf.attendify.feature.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun DefaultBackButton(navController: NavController? = null) {
    IconButton(onClick = {
        navController?.popBackStack()
    }) {
        Icon(
            imageVector = Icons.Rounded.ArrowBack,
            contentDescription = "Back",
        )
    }
}