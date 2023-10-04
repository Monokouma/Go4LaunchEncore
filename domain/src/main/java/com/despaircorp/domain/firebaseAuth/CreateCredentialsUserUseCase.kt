package com.despaircorp.domain.firebaseAuth

import javax.inject.Inject

class CreateCredentialsUserUseCase @Inject constructor(
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository,
    
    ) {
    suspend fun invoke(mail: String, password: String): Boolean =
        firebaseAuthDomainRepository.createUserWithCredentials(mail, password)
}