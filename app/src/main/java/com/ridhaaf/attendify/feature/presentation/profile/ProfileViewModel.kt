package com.ridhaaf.attendify.feature.presentation.profile

import android.content.Context
import android.net.Uri
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
class ProfileViewModel @Inject constructor(
    private val useCase: AuthUseCase,
) : ViewModel() {
    private val _state = mutableStateOf(ProfileState())
    val state: State<ProfileState> = _state

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
            useCase.getCurrentUser().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(
                            isUserLoading = true,
                        )
                    }

                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            isUserLoading = false,
                            userSuccess = result.data,
                        )
                    }

                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            isUserLoading = false,
                            userSuccess = null,
                            userError = result.message ?: "Oops, something went wrong!",
                        )
                    }
                }
            }
        }
    }

    private fun uploadProfilePhoto(context: Context, photo: Uri) {
        viewModelScope.launch {
            useCase.uploadProfilePhoto(context, photo).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(
                            isUploadPhotoLoading = true,
                        )
                    }

                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            isUploadPhotoLoading = false,
                            uploadPhotoSuccess = true,
                        )
                        refresh()
                    }

                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            isUploadPhotoLoading = false,
                            uploadPhotoSuccess = false,
                            uploadPhotoError = result.message ?: "Oops, something went wrong!",
                        )
                    }
                }
            }
        }
    }

    private fun deleteProfilePhoto() {
        viewModelScope.launch {
            useCase.deleteProfilePhoto().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(
                            isDeletePhotoLoading = true,
                        )
                    }

                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            isDeletePhotoLoading = false,
                            deletePhotoSuccess = true,
                        )
                        refresh()
                    }

                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            isDeletePhotoLoading = false,
                            deletePhotoSuccess = false,
                            deletePhotoError = result.message ?: "Oops, something went wrong!",
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.Refresh -> {
                refresh()
            }

            is ProfileEvent.UploadPhoto -> {
                uploadProfilePhoto(event.context, event.photo)
            }

            is ProfileEvent.DeletePhoto -> {
                deleteProfilePhoto()
            }
        }
    }
}