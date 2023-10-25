package com.despaircorp.domain.firebaseStorage

import android.net.Uri

interface FirebaseStorageDomainRepository {
    suspend fun updateUserImage(uid: String, image: Uri): String
}