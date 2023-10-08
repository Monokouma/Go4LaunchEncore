package com.despaircorp.domain.firestore

import javax.inject.Inject

class UpdateUsernameUseCase @Inject constructor(
    private val firestoreDomainRepository: FirestoreDomainRepository,
    
    ) {
    suspend fun invoke(username: String, uid: String): Boolean =
        firestoreDomainRepository.updateUsername(username, uid)
}