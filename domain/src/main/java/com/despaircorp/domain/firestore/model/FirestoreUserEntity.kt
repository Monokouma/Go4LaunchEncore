package com.despaircorp.domain.firestore.model

data class FirestoreUserEntity(
    val picture: String,
    val displayName: String,
    val mailAddress: String,
    val uid: String,
    val currentlyEating: Boolean,
    val eatingPlaceId: String?,
)