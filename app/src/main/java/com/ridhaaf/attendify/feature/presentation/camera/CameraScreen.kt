package com.ridhaaf.attendify.feature.presentation.camera

import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.ridhaaf.attendify.feature.presentation.components.DefaultBackButton
import com.ridhaaf.attendify.feature.presentation.components.defaultToast
import java.util.concurrent.Executor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
) {
    val context: Context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val cameraController: LifecycleCameraController =
        remember { LifecycleCameraController(context) }

    val requestCameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                cameraController.bindToLifecycle(lifecycleOwner)
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
            cameraController.bindToLifecycle(lifecycleOwner)
        } else {
            requestCameraPermissionLauncher.launch(CAMERA)
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("") },
            navigationIcon = {
                DefaultBackButton(navController)
            },
        )
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = {
                val mainExecutor: Executor = ContextCompat.getMainExecutor(context)

                cameraController.takePicture(
                    mainExecutor,
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            image.close()
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Log.e("CameraContent", "Error capturing image", exception)
                        }
                    },
                )
            },
        ) {
            Icon(
                imageVector = Icons.Rounded.Camera, contentDescription = "Camera"
            )
        }
    }) {
        Box(
            modifier = modifier.padding(it),
        ) {
            Column(
                modifier = modifier.fillMaxSize(),
            ) {
                AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
                    PreviewView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_START
                    }.also { previewView ->
                        previewView.controller = cameraController
                        cameraController.bindToLifecycle(lifecycleOwner)
                    }
                })
            }
        }
    }
}