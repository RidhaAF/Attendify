package com.ridhaaf.attendify.feature.presentation.location

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.ridhaaf.attendify.core.utils.OfficeLocation
import com.ridhaaf.attendify.core.utils.isInRadius
import com.ridhaaf.attendify.feature.presentation.components.DefaultBackButton
import com.ridhaaf.attendify.feature.presentation.components.DefaultButton
import com.ridhaaf.attendify.feature.presentation.components.DefaultSpacer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(
    modifier: Modifier = Modifier,
    viewModel: LocationViewModel = hiltViewModel(),
    navController: NavController? = null,
) {
    val state = viewModel.state.value
    val location = state.location
    val context = LocalContext.current

    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.onEvent(LocationEvent.GetEmployeeLocation(fusedLocationProviderClient))
            }
        }

    LaunchedEffect(Unit) {
        val isPermissionGranted = ContextCompat.checkSelfPermission(
            context, ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (isPermissionGranted) {
            viewModel.onEvent(LocationEvent.GetEmployeeLocation(fusedLocationProviderClient))
        } else {
            requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Location") },
                navigationIcon = {
                    DefaultBackButton(navController)
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
                    modifier = Modifier.weight(0.7f),
                ) {
                    MapsContent()
                }
                LocationContent(location)
            }
        }
    }
}

@Composable
private fun MapsContent() {
    val officeLocation = LatLng(OfficeLocation.LATITUDE, OfficeLocation.LONGITUDE)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(officeLocation, 30f)
    }
    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                maxZoomPreference = 30f,
                minZoomPreference = 5f,
            )
        )
    }
    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                mapToolbarEnabled = true,
            )
        )
    }

    GoogleMap(
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        uiSettings = mapUiSettings,
    ) {
        AdvancedMarker(
            state = MarkerState(position = officeLocation),
            title = "Office",
        )
    }
}

@Composable
private fun LocationContent(location: Location? = null) {
    val isInsideRadius = location?.let { isInRadius(it) } ?: false
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