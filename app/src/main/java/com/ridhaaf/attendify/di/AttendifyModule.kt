package com.ridhaaf.attendify.di

import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ridhaaf.attendify.feature.data.repositories.attendance.AttendanceRepositoryImpl
import com.ridhaaf.attendify.feature.data.repositories.auth.AuthRepositoryImpl
import com.ridhaaf.attendify.feature.domain.repositories.attendance.AttendanceRepository
import com.ridhaaf.attendify.feature.domain.repositories.auth.AuthRepository
import com.ridhaaf.attendify.feature.domain.usecases.attendance.AttendanceUseCase
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
    fun provideFirebaseAppCheck(): FirebaseAppCheck {
        return FirebaseAppCheck.getInstance()
    }

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
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
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

    @Provides
    @Singleton
    fun provideAttendanceUseCase(
        repository: AttendanceRepository,
    ): AttendanceUseCase {
        return AttendanceUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAttendanceRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
    ): AttendanceRepository {
        return AttendanceRepositoryImpl(auth, firestore, storage)
    }
}