package com.ridhaaf.attendify.feature.presentation.auth.sign_up

import com.google.firebase.auth.AuthResult

data class SignUpState(
    val isSignUpLoading: Boolean = false,
    val signUpSuccess: AuthResult? = null,
    val signUpError: String = "",
)
