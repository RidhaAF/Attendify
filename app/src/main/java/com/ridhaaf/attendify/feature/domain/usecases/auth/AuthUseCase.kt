package com.ridhaaf.attendify.feature.domain.usecases.auth

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.ridhaaf.attendify.core.utils.Resource
import com.ridhaaf.attendify.feature.data.models.auth.User
import com.ridhaaf.attendify.feature.domain.repositories.auth.AuthRepository
import kotlinx.coroutines.flow.Flow

class AuthUseCase(private val repository: AuthRepository) {
    fun isAuthenticated(): Flow<Resource<Boolean>> {
        return repository.isAuthenticated()
    }

    fun signUp(
        name: String,
        email: String,
        password: String,
    ): Flow<Resource<AuthResult>> {
        return repository.signUp(name, email, password)
    }

    fun signIn(
        email: String,
        password: String,
    ): Flow<Resource<AuthResult>> {
        return repository.signIn(email, password)
    }

    fun signInWithGoogle(credential: AuthCredential): Flow<Resource<AuthResult>> {
        return repository.signInWithGoogle(credential)
    }

    fun signOut(): Flow<Resource<Unit>> {
        return repository.signOut()
    }

    fun getCurrentUser(): Flow<Resource<User>> {
        return repository.getCurrentUser()
    }

    fun uploadProfilePhoto(context: Context, photo: Uri): Flow<Resource<Boolean>> {
        return repository.uploadProfilePhoto(context, photo)
    }

    fun deleteProfilePhoto(): Flow<Resource<Boolean>> {
        return repository.deleteProfilePhoto()
    }
}