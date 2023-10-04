package com.despaircorp.domain.firestore

import javax.inject.Inject

class IsFirestoreUserExistUseCase @Inject constructor(
    private val firestoreDomainRepository: FirestoreDomainRepository,
) {
    suspend fun invoke(uid: String): Boolean = firestoreDomainRepository.isUserExist(uid)
}