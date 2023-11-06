package com.despaircorp.domain.firestore.model

data class CoworkersChatEntity(
    val uid: String,
    val pictureUrl: String,
    val name: String,
    val isOnline: Boolean
)