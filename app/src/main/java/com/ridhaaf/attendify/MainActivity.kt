package com.ridhaaf.attendify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ridhaaf.attendify.feature.presentation.auth.sign_in.SignInScreen
import com.ridhaaf.attendify.feature.presentation.auth.sign_up.SignUpScreen
import com.ridhaaf.attendify.feature.presentation.routes.Routes
import com.ridhaaf.attendify.ui.theme.AttendifyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    AttendifyTheme {
        App()
    }
}