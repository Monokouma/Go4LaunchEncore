package com.despaircorp.data.firebaseStorage

import android.net.Uri
import com.despaircorp.data.utils.CoroutineDispatcherProvider
import com.despaircorp.domain.firebaseStorage.FirebaseStorageDomainRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.cancel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume

class FirebaseStorageDataRepository @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val firebaseStorage: FirebaseStorage
) : FirebaseStorageDomainRepository {
    override suspend fun updateUserImage(uid: String, image: Uri): String =
        withContext(coroutineDispatcherProvider.io) {
            suspendCancellableCoroutine { cont ->
                firebaseStorage.reference.child(uid).putFile(image)
                    .addOnCompleteListener { task ->
                        if (task.exception == null) {
                            if (task.isSuccessful) {
                                firebaseStorage.reference.child(uid).downloadUrl.addOnSuccessListener {
                                    cont.resume(it.toString())
                                }
                            } else {
                                cancel()
                            }
                            
                        } else {
                            cancel()
                        }
                        
                    }
            }
        }
    
}