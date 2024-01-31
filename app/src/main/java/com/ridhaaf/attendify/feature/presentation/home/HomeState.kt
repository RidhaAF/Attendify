package com.ridhaaf.attendify.feature.presentation.home

import com.ridhaaf.attendify.feature.data.models.auth.User

data class HomeState(
    val isUserLoading: Boolean = false,
    val userSuccess: User? = null,
    val userError: String = "",
    val isSignOutLoading: Boolean = false,
    val signOutSuccess: Boolean = false,
    val signOutError: String = "",
)
