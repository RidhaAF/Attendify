package com.ridhaaf.attendify

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import com.ridhaaf.attendify.feature.presentation.auth.sign_in.SignInScreen
import com.ridhaaf.attendify.feature.presentation.auth.sign_up.SignUpScreen
import com.ridhaaf.attendify.feature.presentation.biometric.BiometricScreen
import com.ridhaaf.attendify.feature.presentation.camera.CameraScreen
import com.ridhaaf.attendify.feature.presentation.home.HomeScreen
import com.ridhaaf.attendify.feature.presentation.location.LocationScreen
import com.ridhaaf.attendify.feature.presentation.routes.Routes
import com.ridhaaf.attendify.ui.theme.AttendifyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )
        setContent {
            AttendifyTheme(
                dynamicColor = false,
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    App()
                }
            }
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.SIGN_IN,
    ) {
        composable(Routes.SIGN_IN) {
            SignInScreen(
                navController = navController,
            )
        }
        composable(Routes.SIGN_UP) {
            SignUpScreen(
                navController = navController,
            )
        }
        composable(Routes.BIOMETRIC) {
            BiometricScreen(
                navController = navController,
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                navController = navController,
            )
        }
        composable(
            Routes.LOCATION, arguments = listOf(
                navArgument("status") {
                    type = NavType.BoolType
                },
                navArgument("dateTime") {
                    type = NavType.LongType
                },
            )
        ) {
            val status = navBackStackEntry?.arguments?.getBoolean("status") ?: false
            val dateTime = navBackStackEntry?.arguments?.getLong("dateTime")

            LocationScreen(
                navController = navController,
                status = status,
                dateTime = dateTime,
            )
        }
        composable(
            Routes.CAMERA,
            arguments = listOf(
                navArgument("status") {
                    type = NavType.BoolType
                },
                navArgument("dateTime") {
                    type = NavType.LongType
                },
                navArgument("latitude") {
                    type = NavType.StringType
                },
                navArgument("longitude") {
                    type = NavType.StringType
                },
            ),
        ) {
            val status = navBackStackEntry?.arguments?.getBoolean("status") ?: false
            val dateTime = navBackStackEntry?.arguments?.getLong("dateTime")
            val latitude = navBackStackEntry?.arguments?.getString("latitude")
            val longitude = navBackStackEntry?.arguments?.getString("longitude")

            CameraScreen(
                navController = navController,
                status = status,
                dateTime = dateTime,
                latitude = latitude?.toDouble(),
                longitude = longitude?.toDouble(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    AttendifyTheme {
        App()
    }
}