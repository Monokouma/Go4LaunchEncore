package com.despaircorp.domain.firestore

import javax.inject.Inject

class UpdateFirestoreMailAddressUseCase @Inject constructor(
    private val firestoreDomainRepository: FirestoreDomainRepository,
    
    ) {
    suspend fun invoke(uid: String, newMailAddress: String): Boolean =
        firestoreDomainRepository.updateMailAddress(uid, newMailAddress)
}