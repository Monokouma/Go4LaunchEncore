package com.despaircorp.domain.firebaseStorage

import android.net.Uri
import javax.inject.Inject

class UpdateUserImageThenGetLinkUseCase @Inject constructor(
    private val firebaseStorageDomainRepository: FirebaseStorageDomainRepository,
    
    ) {
    suspend fun invoke(uid: String, image: Uri): String =
        firebaseStorageDomainRepository.updateUserImage(uid, image)
}