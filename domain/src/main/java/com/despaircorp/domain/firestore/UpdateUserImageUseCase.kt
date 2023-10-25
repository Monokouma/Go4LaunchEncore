package com.despaircorp.domain.firestore

import javax.inject.Inject

class UpdateUserImageUseCase @Inject constructor(
    private val firestoreDomainRepository: FirestoreDomainRepository,
    
    ) {
    suspend fun invoke(uid: String, pictureUrl: String): Boolean =
        firestoreDomainRepository.updateUserImage(uid, pictureUrl)
}