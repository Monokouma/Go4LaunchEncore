package com.despaircorp.domain.firebaseAuth

import javax.inject.Inject

class IsUserWithCredentialsSignedInUseCase @Inject constructor(
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository,
    
    ) {
    suspend fun invoke(mail: String, password: String): Boolean =
        firebaseAuthDomainRepository.isUserWithCredentialsExist(mail, password)
}