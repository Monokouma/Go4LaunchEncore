package com.despaircorp.domain.firebase_storage

import android.net.Uri

interface FirebaseStorageDomainRepository {
    suspend fun updateUserImage(uid: String, image: Uri): String?
}