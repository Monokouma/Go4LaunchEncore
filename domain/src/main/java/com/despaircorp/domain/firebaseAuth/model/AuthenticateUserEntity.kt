package com.despaircorp.domain.firebaseAuth.model

data class AuthenticateUserEntity(
    val picture: String,
    val displayName: String,
    val mailAddress: String,
    val uid: String,
)