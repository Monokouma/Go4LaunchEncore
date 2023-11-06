package com.despaircorp.domain.firestore

import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import javax.inject.Inject

class ChangePresenceUseCase @Inject constructor(
    private val firestoreDomainRepository: FirestoreDomainRepository,
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository
) {
    suspend fun invoke(isPresent: Boolean) {
        firestoreDomainRepository.updateUserPresence(
            uid = firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid, // TODO MONO
            isPresent = isPresent
        )
    }
}