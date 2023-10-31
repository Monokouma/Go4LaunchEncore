package com.despaircorp.domain.firebase_auth

import javax.inject.Inject

class IsUserWithCredentialsSignedInUseCase @Inject constructor(
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository,
    
    ) {
    suspend fun invoke(mail: String, password: String): Boolean =
        firebaseAuthDomainRepository.isUserWithCredentialsExist(mail, password)
}