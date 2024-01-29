package com.ridhaaf.attendify.feature.presentation.location

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ridhaaf.attendify.feature.presentation.components.DefaultBackButton
import com.ridhaaf.attendify.feature.presentation.components.DefaultButton
import com.ridhaaf.attendify.feature.presentation.components.DefaultSpacer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Location") },
                navigationIcon = {
                    DefaultBackButton(navController)
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Rounded.MyLocation,
                            contentDescription = "My location",
                        )
                    }
                },
            )
        },
    ) {
        Box(
            modifier = modifier.padding(it),
        ) {
            Column(
                modifier = modifier.fillMaxSize(),
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.7f)
                        .padding(16.dp),
                ) {
                    MapsContent()
                }
                LocationContent()
            }
        }
    }
}

@Composable
private fun MapsContent() {
    Column {
        Text("Maps")
    }
}

@Composable
private fun LocationContent() {
    val isInsideRadius = true
    val text = if (isInsideRadius) "You're in the radius area" else "You're not in the radius area"


    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onSecondary)
            .padding(16.dp),
    ) {
        Text(
            "Checking your location...",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        DefaultSpacer(size = 8)
        Text(text)
        DefaultSpacer()
        DefaultButton(
            onClick = {},
            enabled = isInsideRadius,
        ) {
            Text("Next")
        }
    }
}