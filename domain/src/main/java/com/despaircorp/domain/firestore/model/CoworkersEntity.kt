package com.despaircorp.domain.firestore.model

data class CoworkersEntity(
    val uid: String,
    val isEating: Boolean,
    val eatingPlaceId: String?,
    val pictureUrl: String,
    val name: String,
    
    )