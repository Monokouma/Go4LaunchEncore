package com.despaircorp.domain.firebaseAuth

import javax.inject.Inject

class IsUserAlreadyAuthUseCase @Inject constructor(
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository,
    
    ) {
    suspend fun invoke(): Boolean = firebaseAuthDomainRepository.isUserAlreadyAuth()
}