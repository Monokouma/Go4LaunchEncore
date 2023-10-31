package com.despaircorp.data.firebase_auth.dto

import android.net.Uri
import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties

@Keep
@IgnoreExtraProperties
data class AuthenticateUserDto(
    val displayName: String? = null,
    val emailAddress: String? = null,
    val picture: Uri? = null,
    val uid: String? = null
)