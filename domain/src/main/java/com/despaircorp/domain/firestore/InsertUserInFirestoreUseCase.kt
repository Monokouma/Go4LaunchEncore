package com.despaircorp.domain.firestore

import com.despaircorp.domain.firebaseAuth.model.AuthenticateUserEntity
import javax.inject.Inject

class InsertUserInFirestoreUseCase @Inject constructor(
    private val firestoreDomainRepository: FirestoreDomainRepository,
    
    ) {
    suspend fun invoke(authenticateUserEntity: AuthenticateUserEntity): Boolean =
        firestoreDomainRepository.insertUser(authenticateUserEntity)
}