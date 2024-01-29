package com.ridhaaf.attendify.feature.presentation.home

sealed class HomeEvent {
    data object SignOut : HomeEvent()
}