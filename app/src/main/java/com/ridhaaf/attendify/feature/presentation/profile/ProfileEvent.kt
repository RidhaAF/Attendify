package com.ridhaaf.attendify.feature.presentation.profile

sealed class ProfileEvent {
    data object Refresh : ProfileEvent()
}