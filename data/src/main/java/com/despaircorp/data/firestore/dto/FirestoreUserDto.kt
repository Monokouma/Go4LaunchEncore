package com.despaircorp.data.firestore.dto

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties

@Keep
@IgnoreExtraProperties
data class FirestoreUserDto(
    val displayName: String? = null,
    val mailAddress: String? = null,
    val picture: String? = null,
    val uid: String? = null,
    val currentlyEating: Boolean? = null,
    val eatingPlaceId: String? = null,
)