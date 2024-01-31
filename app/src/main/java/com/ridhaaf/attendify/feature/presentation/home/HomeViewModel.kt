package com.ridhaaf.attendify.feature.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ridhaaf.attendify.core.utils.Resource
import com.ridhaaf.attendify.feature.domain.usecases.auth.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
) : ViewModel() {
    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private val _isRefreshing = mutableStateOf(false)
    val isRefreshing: State<Boolean> = _isRefreshing

    init {
        refresh()
    }

    private fun refresh() {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            authUseCase.getCurrentUser().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = HomeState(
                            isUserLoading = true,
                        )
                    }

                    is Resource.Success -> {
                        _state.value = HomeState(
                            isUserLoading = false,
                            userSuccess = result.data,
                        )
                    }

                    is Resource.Error -> {
                        _state.value = HomeState(
                            isUserLoading = false,
                            userSuccess = null,
                            userError = result.message ?: "Oops, something went wrong!",
                        )
                    }
                }
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            authUseCase.signOut().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = HomeState(
                            isSignOutLoading = true,
                        )
                    }

                    is Resource.Success -> {
                        _state.value = HomeState(
                            isSignOutLoading = false,
                            signOutSuccess = true,
                        )
                    }

                    is Resource.Error -> {
                        _state.value = HomeState(
                            isSignOutLoading = false,
                            signOutSuccess = false,
                            signOutError = result.message ?: "Oops, something went wrong!",
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Refresh -> {
                refresh()
            }

            is HomeEvent.SignOut -> {
                signOut()
            }
        }
    }
}