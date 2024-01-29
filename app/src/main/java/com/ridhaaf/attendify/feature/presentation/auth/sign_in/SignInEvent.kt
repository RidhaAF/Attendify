package com.ridhaaf.attendify.feature.presentation.auth.sign_in

import com.google.firebase.auth.AuthCredential

sealed class SignInEvent {
    data class Email(val email: String) : SignInEvent()
    data class Password(val password: String) : SignInEvent()
    data object SignIn : SignInEvent()
    data class GoogleSignIn(val credential: AuthCredential) : SignInEvent()
}