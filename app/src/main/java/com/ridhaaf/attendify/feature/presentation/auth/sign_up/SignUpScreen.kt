package com.ridhaaf.attendify.feature.presentation.auth.sign_up

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
import com.ridhaaf.attendify.feature.presentation.components.DefaultButton
import com.ridhaaf.attendify.feature.presentation.components.DefaultSpacer
import com.ridhaaf.attendify.feature.presentation.components.DefaultTextField
import com.ridhaaf.attendify.feature.presentation.components.defaultToast
import com.ridhaaf.attendify.feature.presentation.routes.Routes
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
    navController: NavController? = null,
) {
    val state = viewModel.state.value
    val error = state.signUpError
    val context = LocalContext.current

    LaunchedEffect(key1 = error) {
        if (error.isNotBlank()) {
            defaultToast(context, error)
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
                NameTextField(viewModel)
                DefaultSpacer()
                EmailTextField(viewModel)
                DefaultSpacer()
                PasswordTextField(viewModel)
                DefaultSpacer()
                ConfirmPasswordTextField(viewModel)
                DefaultSpacer()
                SignUpButton(state, viewModel, navController)
                DefaultSpacer()
                RedirectToSignIn(navController)
            }
        }
    }
}

@Composable
private fun TitleText(title: String = "Sign Up") {
    Text(title)
}

@Composable
fun NameTextField(viewModel: SignUpViewModel) {
    DefaultTextField(
        value = viewModel.name,
        onValueChange = { viewModel.onEvent(SignUpEvent.Name(it)) },
        placeholder = "Name",
    )
}

@Composable
fun EmailTextField(viewModel: SignUpViewModel) {
    DefaultTextField(
        value = viewModel.email,
        onValueChange = { viewModel.onEvent(SignUpEvent.Email(it)) },
        placeholder = "Email",
    )
}

@Composable
fun PasswordTextFieldContent(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }

    DefaultTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        isObscure = !passwordVisibility,
        trailingIcon = {
            IconButton(
                onClick = {
                    passwordVisibility = !passwordVisibility
                },
            ) {
                val icon = if (passwordVisibility) Icons.Rounded.VisibilityOff
                else Icons.Rounded.Visibility
                val contentDescription =
                    if (passwordVisibility) "Hide ${placeholder.lowercase(Locale.getDefault())}"
                    else "Show ${placeholder.lowercase(Locale.getDefault())}"

                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                )
            }
        },
    )
}

@Composable
fun PasswordTextField(viewModel: SignUpViewModel) {
    PasswordTextFieldContent(
        value = viewModel.password,
        onValueChange = { viewModel.onEvent(SignUpEvent.Password(it)) },
        placeholder = "Password",
    )
}

@Composable
fun ConfirmPasswordTextField(viewModel: SignUpViewModel) {
    PasswordTextFieldContent(
        value = viewModel.confirmPassword,
        onValueChange = { viewModel.onEvent(SignUpEvent.ConfirmPassword(it)) },
        placeholder = "Confirm Password",
    )
}

@Composable
fun SignUpButton(
    state: SignUpState,
    viewModel: SignUpViewModel,
    navController: NavController?,
) {
    val text = if (state.isSignUpLoading) "Signing up..." else "Sign up"

    DefaultButton(
        onClick = { viewModel.onEvent(SignUpEvent.SignUp) },
        child = { Text(text) },
    )

    LaunchedEffect(key1 = state.signUpSuccess) {
        if (state.signUpSuccess != null) {
            redirectAfterSignUp(navController)
        }
    }
}

@Composable
fun RedirectToSignIn(navController: NavController?) {
    TextButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = { navController?.popBackStack() },
    ) {
        Text("Already have an account? Sign In")
    }
}

private fun redirectAfterSignUp(navController: NavController?) {
    navController?.navigate(Routes.HOME)
}