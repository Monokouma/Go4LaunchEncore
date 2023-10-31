package com.despaircorp.domain.firebase_auth

import javax.inject.Inject

class IsUserAlreadyAuthUseCase @Inject constructor(
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository,
    
    ) {
    suspend fun invoke(): Boolean = firebaseAuthDomainRepository.isUserAlreadyAuth()
}