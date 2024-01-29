package com.ridhaaf.attendify.feature.presentation.auth.sign_in

import com.google.firebase.auth.AuthResult

data class SignInState(
    val isSignInLoading: Boolean = false,
    val signInSuccess: AuthResult? = null,
    val signInError: String = "",
    val isGoogleSignInLoading: Boolean = false,
    val googleSignInSuccess: AuthResult? = null,
    val googleSignInError: String = "",
)
