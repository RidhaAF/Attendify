package com.ridhaaf.attendify.feature.presentation.biometric

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.ridhaaf.attendify.feature.presentation.components.defaultToast
import com.ridhaaf.attendify.feature.presentation.routes.Routes

@Composable
fun BiometricScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
) {
    val context = LocalContext.current
    val biometricManager = remember { BiometricManager.from(context) }
    val isBiometricAvailable = remember {
        biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
    }
    when (isBiometricAvailable) {
        BiometricManager.BIOMETRIC_SUCCESS -> {
            // Biometric features are available
        }

        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
            // No biometric features available on this device
            navigateToHome(navController)
        }

        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
            // Biometric features are currently unavailable.
            navigateToHome(navController)
        }

        BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
            // Biometric features available but a security vulnerability has been discovered
        }

        BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
            // Biometric features are currently unavailable because the specified options are incompatible with the current Android version..
        }

        BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
            // Unable to determine whether the user can authenticate using biometrics
        }

        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
            // The user can't authenticate because no biometric or device credential is enrolled.
            defaultToast(
                context,
                "No biometric or device credential is enrolled. Please add one in settings."
            )
        }
    }

    val executor = remember { ContextCompat.getMainExecutor(context) }
    val bioPrompt = BiometricPrompt(context as FragmentActivity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                println("Biometric authentication error: $errString")
            }

            @RequiresApi(Build.VERSION_CODES.R)
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                navigateToHome(navController)
                println("Biometric authentication succeeded")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                println("Biometric authentication failed")
            }
        })

    val promptInfo = BiometricPrompt.PromptInfo.Builder().setAllowedAuthenticators(BIOMETRIC_STRONG)
        .setTitle("Biometric Authentication")
        .setSubtitle("Authenticate using your fingerprint or face recognition")
        .setDescription("Please authenticate to proceed").setNegativeButtonText("Cancel")
        .setConfirmationRequired(true).build()

    DisposableEffect(context) {
        bioPrompt.authenticate(promptInfo)

        onDispose {
            bioPrompt.cancelAuthentication()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(onClick = { bioPrompt.authenticate(promptInfo) }) {
            Icon(
                imageVector = Icons.Rounded.Fingerprint,
                contentDescription = "Biometric Authentication",
                modifier = modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

private fun navigateToHome(navController: NavController?) {
    navController?.navigate(Routes.HOME) {
        popUpTo(Routes.BIOMETRIC) {
            inclusive = true
        }
    }
}