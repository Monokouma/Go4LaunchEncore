package com.despaircorp.domain.firebaseAuth

import javax.inject.Inject

class UpdateAuthPasswordUseCase @Inject constructor(
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository,
    
    ) {
    suspend fun invoke(newPassword: String): Boolean =
        firebaseAuthDomainRepository.updatePassword(newPassword)
}