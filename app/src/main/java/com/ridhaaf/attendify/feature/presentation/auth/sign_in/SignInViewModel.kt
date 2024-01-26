package com.ridhaaf.attendify.feature.presentation.auth.sign_in

import android.util.Patterns
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.ridhaaf.attendify.core.utils.Resource
import com.ridhaaf.attendify.feature.domain.usecases.auth.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val useCase: AuthUseCase,
) : ViewModel() {
    private val _state = mutableStateOf(SignInState())
    val state: State<SignInState> = _state

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    private var isAuthenticated by mutableStateOf(false)

    fun isAuth(): Boolean {
        viewModelScope.launch {
            useCase.isAuthenticated().collect { result ->
                isAuthenticated = when (result) {
                    is Resource.Success -> {
                        result.data ?: false
                    }

                    else -> {
                        false
                    }
                }
            }
        }
        return isAuthenticated
    }

    private fun signIn(email: String, password: String) {
        viewModelScope.launch {
            useCase.signIn(email, password).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = SignInState(
                            isSignInLoading = true,
                        )
                    }

                    is Resource.Success -> {
                        _state.value = SignInState(
                            isSignInLoading = false,
                            signInSuccess = result.data,
                        )
                    }

                    is Resource.Error -> {
                        _state.value = SignInState(
                            isSignInLoading = false,
                            signInSuccess = null,
                            signInError = result.message ?: "Oops, something went wrong!",
                        )
                    }
                }
            }
        }
    }

    private fun signInWithGoogle(credential: AuthCredential) {
        viewModelScope.launch {
            useCase.signInWithGoogle(credential).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = SignInState(
                            isGoogleSignInLoading = true,
                        )
                    }

                    is Resource.Success -> {
                        _state.value = SignInState(
                            isGoogleSignInLoading = false,
                            googleSignInSuccess = result.data,
                        )
                    }

                    is Resource.Error -> {
                        _state.value = SignInState(
                            isGoogleSignInLoading = false,
                            googleSignInSuccess = null,
                            googleSignInError = result.message ?: "Oops, something went wrong!",
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.Email -> {
                email = event.email
            }

            is SignInEvent.Password -> {
                password = event.password
            }

            is SignInEvent.SignIn -> {
                if (email.isEmpty() || password.isEmpty()) {
                    _state.value = SignInState(
                        signInError = "Please fill in the fields",
                    )
                    return
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    _state.value = SignInState(
                        signInError = "Please enter a valid email address",
                    )
                    return
                } else if (password.length < 8) {
                    _state.value = SignInState(
                        signInError = "Password must be at least 8 characters",
                    )
                    return
                }
                signIn(email, password)
            }

            is SignInEvent.GoogleSignIn -> {
                signInWithGoogle(event.credential)
            }
        }
    }

    fun resetState() {
        _state.value = SignInState()
    }
}