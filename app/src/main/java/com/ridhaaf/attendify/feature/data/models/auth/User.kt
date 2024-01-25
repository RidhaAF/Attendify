package com.ridhaaf.attendify.feature.data.models.auth

data class User(
    val id: String = "",
    var displayName: String = "",
    var email: String = "",
    var photoUrl: String? = null,
    var createdAt: Long = 0L,
)