package com.despaircorp.domain.firestore

import javax.inject.Inject

class ChangePresenceUseCase @Inject constructor(
    private val firestoreDomainRepository: FirestoreDomainRepository
) {
    suspend fun invoke(isPresent: Boolean) {
        firestoreDomainRepository.updateUserPresence(
            uid = "uid", // TODO MONO
            isPresent = isPresent
        )
    }
}