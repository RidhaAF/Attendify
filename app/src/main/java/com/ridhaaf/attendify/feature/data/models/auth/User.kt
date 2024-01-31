package com.ridhaaf.attendify.feature.data.models.auth

data class User(
    var id: String = "",
    var displayName: String = "",
    var email: String = "",
    var photoUrl: String? = null,
    var status: Boolean = false,
    var createdAt: Long = 0L,
)