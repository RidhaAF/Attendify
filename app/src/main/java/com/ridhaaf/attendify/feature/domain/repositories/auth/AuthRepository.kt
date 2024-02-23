package com.ridhaaf.attendify.feature.domain.repositories.auth

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.ridhaaf.attendify.core.utils.Resource
import com.ridhaaf.attendify.feature.data.models.auth.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun isAuthenticated(): Flow<Resource<Boolean>>

    fun signUp(
        name: String,
        email: String,
        password: String,
    ): Flow<Resource<AuthResult>>

    fun signIn(
        email: String,
        password: String,
    ): Flow<Resource<AuthResult>>

    fun signInWithGoogle(credential: AuthCredential): Flow<Resource<AuthResult>>

    fun signOut(): Flow<Resource<Unit>>

    fun getCurrentUser(): Flow<Resource<User>>

    fun uploadProfilePhoto(context: Context, photo: Uri): Flow<Resource<Boolean>>

    fun deleteProfilePhoto(): Flow<Resource<Boolean>>
}