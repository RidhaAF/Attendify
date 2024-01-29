package com.ridhaaf.attendify.feature.presentation.home

import com.ridhaaf.attendify.feature.data.models.auth.User

data class HomeState(
    val isLoading: Boolean = false,
    val error: String = "",
    val isUserLoading: Boolean = false,
    val userSuccess: User? = null,
    val userError: String = "",
    val isSignOutLoading: Boolean = false,
    val signOutSuccess: Boolean = false,
    val signOutError: String = "",
)
