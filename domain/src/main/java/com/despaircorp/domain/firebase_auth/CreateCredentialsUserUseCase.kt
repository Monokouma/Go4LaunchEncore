package com.despaircorp.domain.firebase_auth

import javax.inject.Inject

class CreateCredentialsUserUseCase @Inject constructor(
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository,
    
    ) {
    suspend fun invoke(mail: String, password: String): Boolean =
        firebaseAuthDomainRepository.createUserWithCredentials(mail, password)
}