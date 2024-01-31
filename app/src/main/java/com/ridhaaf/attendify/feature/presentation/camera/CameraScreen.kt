package com.ridhaaf.attendify.feature.presentation.camera

import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.ridhaaf.attendify.BuildConfig
import com.ridhaaf.attendify.feature.presentation.components.DefaultBackButton
import com.ridhaaf.attendify.feature.presentation.components.defaultToast
import com.ridhaaf.attendify.feature.presentation.routes.Routes
import java.io.File
import java.text.DateFormat.getDateInstance
import java.util.Date
import java.util.Objects

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel(),
    navController: NavController? = null,
    status: Boolean = false,
    dateTime: Long? = 0L,
    latitude: Double? = 0.0,
    longitude: Double? = 0.0,
) {
    val state = viewModel.state.value
    val clockIn = state.clockIn
    val clockInError = state.clockInError
    val clockOut = state.clockOut
    val clockOutError = state.clockOutError
    val context: Context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context), BuildConfig.APPLICATION_ID + ".provider", file
    )

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            Log.d("CameraScreen", "args: $status, $dateTime, $latitude, $longitude")
            val data = mutableMapOf(
                "status" to status as Any,
                "dateTime" to dateTime as Any,
                "latitude" to latitude as Any,
                "longitude" to longitude as Any,
            ).toMap()
            Log.d("CameraScreen", "data: $data")

            capturedImageUri = uri

            if (status) {
                viewModel.onEvent(
                    CameraEvent.ClockOut(context, data, capturedImageUri)
                )
            } else {
                viewModel.onEvent(
                    CameraEvent.ClockIn(context, data, capturedImageUri)
                )
            }
        }
    }

    val requestCameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                cameraLauncher.launch(uri)
            } else {
                defaultToast(
                    context,
                    "Camera permission is required to use camera",
                )
            }
        }

    LaunchedEffect(Unit) {
        val isPermissionGranted = ContextCompat.checkSelfPermission(
            context, CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (isPermissionGranted) {
            cameraLauncher.launch(uri)
        } else {
            requestCameraPermissionLauncher.launch(CAMERA)
        }
    }

    LaunchedEffect(key1 = clockIn, key2 = clockInError) {
        if (clockIn) {
            defaultToast(context, "Clock In Success")
            navController?.navigate(Routes.HOME)
        }

        if (clockInError.isNotBlank()) {
            defaultToast(context, clockInError)
        }
    }

    LaunchedEffect(key1 = clockOut, key2 = clockOutError) {
        if (clockOut) {
            defaultToast(context, "Clock Out Success")
            navController?.navigate(Routes.HOME)
        }

        if (clockOutError.isNotBlank()) {
            defaultToast(context, clockOutError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Take a Selfie") },
                modifier = modifier.background(Color.Transparent),
                navigationIcon = {
                    DefaultBackButton(navController)
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { cameraLauncher.launch(uri) }) {
                Icon(
                    imageVector = Icons.Rounded.CameraAlt,
                    contentDescription = "Camera",
                )
            }
        },
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(it),
        ) {
            if (state.isClockInLoading || state.isClockOutLoading) {
                Column(
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                    Text("Loading...")
                }
            } else {
                if (capturedImageUri.path?.isNotEmpty() == true) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = rememberAsyncImagePainter(capturedImageUri),
                        contentDescription = "Captured Image",
                    )
                }
            }
        }
    }
}

private fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = getDateInstance().format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir /* directory */
    )
}