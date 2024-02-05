package com.ridhaaf.attendify.feature.presentation.biometric

import android.content.Context
import android.os.Build
import android.provider.Settings
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.ridhaaf.attendify.core.utils.navigateToAppSetting
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

    val executor = remember { ContextCompat.getMainExecutor(context) }
    val bioPrompt = BiometricPrompt(
        context as FragmentActivity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                checkBiometric(context)
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
        },
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        .setTitle("Biometric Authentication")
        .setSubtitle("Authenticate using your fingerprint, face recognition or device credential")
        .setDescription("Please authenticate to proceed")
        .setConfirmationRequired(true).build()

    LaunchedEffect(isBiometricAvailable) {
        when (isBiometricAvailable) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // Biometric features are available
                bioPrompt.authenticate(promptInfo)
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // The user can't authenticate because no biometric or device credential is enrolled.
                defaultToast(
                    context,
                    "No biometric or device credential is enrolled. Please add one in Settings"
                )
                checkBiometric(context)
            }

            else -> {
                navigateToHome(navController)
                println("Biometric authentication failed. Please try again later.")
            }
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

private fun checkBiometric(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        context.navigateToAppSetting(Settings.ACTION_BIOMETRIC_ENROLL)
    } else {
        context.navigateToAppSetting(Settings.ACTION_SECURITY_SETTINGS)
    }
}

private fun navigateToHome(navController: NavController?) {
    navController?.navigate(Routes.HOME) {
        popUpTo(Routes.BIOMETRIC) {
            inclusive = true
        }
    }
}