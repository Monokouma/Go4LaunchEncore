package com.despaircorp.domain.firebase_auth.model

data class AuthenticateUserEntity(
    val picture: String,
    val displayName: String,
    val mailAddress: String,
    val uid: String,
    val currentlyEating: Boolean,
    val eatingPlaceId: String?,
)