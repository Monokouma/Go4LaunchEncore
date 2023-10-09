package com.despaircorp.domain.firebaseAuth

import javax.inject.Inject

class DisconnectUserUseCase @Inject constructor(
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository,
    
    ) {
    suspend fun invoke(): Boolean = firebaseAuthDomainRepository.disconnectUser()
}