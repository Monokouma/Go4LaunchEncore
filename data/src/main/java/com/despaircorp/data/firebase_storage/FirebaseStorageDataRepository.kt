package com.despaircorp.data.firebase_storage

import android.net.Uri
import com.despaircorp.data.utils.CoroutineDispatcherProvider
import com.despaircorp.data.utils.safeAwait
import com.despaircorp.domain.firebase_storage.FirebaseStorageDomainRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseStorageDataRepository @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val firebaseStorage: FirebaseStorage
) : FirebaseStorageDomainRepository {
    override suspend fun updateUserImage(uid: String, image: Uri): String? =
        withContext(coroutineDispatcherProvider.io) {
            firebaseStorage.reference.child(uid).putFile(image).safeAwait()
            firebaseStorage.reference.child(uid).downloadUrl.safeAwait()?.toString()
        }
}