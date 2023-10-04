package com.despaircorp.domain.firestore

import com.despaircorp.domain.firestore.model.FirestoreUserEntity
import javax.inject.Inject

class GetFirestoreUserUseCase @Inject constructor(
    private val firestoreDomainRepository: FirestoreDomainRepository,
    
    ) {
    suspend fun invoke(uid: String): FirestoreUserEntity = firestoreDomainRepository.getUser(uid)
}