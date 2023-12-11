package com.despaircorp.domain.firestore.model

data class FirestoreUserEntity(
    val uid: String,
    val displayName: String,
    val picture: String,
    val mailAddress: String,
    val currentlyEating: Boolean,
    val eatingPlaceId: String?,
    val online: Boolean
)