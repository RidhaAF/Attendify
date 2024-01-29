package com.ridhaaf.attendify.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ridhaaf.attendify.feature.data.repositories.auth.AuthRepositoryImpl
import com.ridhaaf.attendify.feature.domain.repositories.auth.AuthRepository
import com.ridhaaf.attendify.feature.domain.usecases.auth.AuthUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AttendifyModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthUseCase(
        repository: AuthRepository,
    ): AuthUseCase {
        return AuthUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
    ): AuthRepository {
        return AuthRepositoryImpl(auth, firestore)
    }
}