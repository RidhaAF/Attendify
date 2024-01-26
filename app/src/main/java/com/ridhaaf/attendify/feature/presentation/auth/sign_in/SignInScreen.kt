package com.ridhaaf.attendify.feature.presentation.auth.sign_in

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.ridhaaf.attendify.R
import com.ridhaaf.attendify.feature.presentation.auth.components.GoogleButton
import com.ridhaaf.attendify.feature.presentation.auth.components.OrSignWith
import com.ridhaaf.attendify.feature.presentation.components.DefaultButton
import com.ridhaaf.attendify.feature.presentation.components.DefaultSpacer
import com.ridhaaf.attendify.feature.presentation.components.DefaultTextField
import com.ridhaaf.attendify.feature.presentation.components.defaultToast
import com.ridhaaf.attendify.feature.presentation.routes.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = hiltViewModel(),
    navController: NavController? = null,
) {
    val state = viewModel.state.value
    val error = state.signInError
    val googleError = state.googleSignInError
    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_CANCELED) {
                defaultToast(context, "Sign in with Google canceled")
                return@rememberLauncherForActivityResult
            }

            val account = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val result = account.getResult(ApiException::class.java)
                val credentials = GoogleAuthProvider.getCredential(result.idToken, null)
                viewModel.onEvent(SignInEvent.GoogleSignIn(credentials))
            } catch (e: ApiException) {
                defaultToast(context, "Sign in with Google failed")
                throw e
            }
        }

    LaunchedEffect(key1 = viewModel.isAuth()) {
        if (viewModel.isAuth()) {
            redirectAfterSignIn(navController)
        }
    }

    LaunchedEffect(key1 = error, key2 = googleError) {
        if (error.isNotBlank()) {
            defaultToast(context, error)
        }

        if (googleError.isNotBlank()) {
            defaultToast(context, googleError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { TitleText() })
        },
    ) {
        Box(modifier = modifier.padding(it)) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                EmailTextField(viewModel)
                DefaultSpacer()
                PasswordTextField(viewModel)
                DefaultSpacer()
                SignInButton(state, viewModel, navController)
                DefaultSpacer()
                OrSignInWith()
                DefaultSpacer()
                GoogleSignInButton(state, context, launcher, navController)
                DefaultSpacer()
                RedirectToSignUp(navController)
            }
        }
    }
}

@Composable
private fun TitleText(title: String = "Sign In") {
    Text(title)
}

@Composable
private fun EmailTextField(viewModel: SignInViewModel) {
    DefaultTextField(
        value = viewModel.email,
        onValueChange = { viewModel.onEvent(SignInEvent.Email(it)) },
        placeholder = "Email",
    )
}

@Composable
private fun PasswordTextField(viewModel: SignInViewModel) {
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }

    DefaultTextField(
        value = viewModel.password,
        onValueChange = { viewModel.onEvent(SignInEvent.Password(it)) },
        placeholder = "Password",
        isObscure = !passwordVisibility,
        trailingIcon = {
            IconButton(
                onClick = { passwordVisibility = !passwordVisibility },
            ) {
                val icon = if (passwordVisibility) Icons.Rounded.VisibilityOff
                else Icons.Rounded.Visibility
                val contentDescription = if (passwordVisibility) "Hide password"
                else "Show password"

                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                )
            }
        },
    )
}

@Composable
private fun SignInButton(
    state: SignInState,
    viewModel: SignInViewModel,
    navController: NavController?,
) {
    val text = if (state.isSignInLoading) "Signing in..." else "Sign in"

    DefaultButton(
        onClick = { viewModel.onEvent(SignInEvent.SignIn) },
        child = { Text(text) },
    )

    LaunchedEffect(key1 = state.signInSuccess) {
        if (state.signInSuccess != null) {
            redirectAfterSignIn(navController)
        }
    }
}

@Composable
private fun OrSignInWith() {
    OrSignWith("Or sign in with")
}

@Composable
private fun GoogleSignInButton(
    state: SignInState,
    context: Context,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    navController: NavController?,
) {
    val text = if (state.isGoogleSignInLoading) "Signing in..." else "Sign in with Google"

    GoogleButton(
        onClick = {
            val googleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                    .requestIdToken(context.getString(R.string.web_client_id)).build()
            val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)

            googleSignInClient.signOut().addOnCompleteListener {
                if (it.isSuccessful) launcher.launch(googleSignInClient.signInIntent)
                else defaultToast(context, "Sign in with Google failed")
            }
        },
        text = text,
    )

    LaunchedEffect(key1 = state.googleSignInSuccess) {
        if (state.googleSignInSuccess != null) {
            redirectAfterSignIn(navController)
        }
    }
}

@Composable
private fun RedirectToSignUp(navController: NavController?) {
    TextButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = { navController?.navigate(Routes.SIGN_UP) },
    ) {
        Text("Don't have an account? Sign Up")
    }
}

private fun redirectAfterSignIn(navController: NavController?) {
    navController?.navigate(Routes.HOME)
}