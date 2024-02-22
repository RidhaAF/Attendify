package com.ridhaaf.attendify.feature.presentation.profile

import com.ridhaaf.attendify.feature.data.models.auth.User

data class ProfileState(
    val isUserLoading: Boolean = false,
    val userSuccess: User? = null,
    val userError: String = "",
)