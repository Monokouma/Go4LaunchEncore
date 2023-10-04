package com.despaircorp.domain.firebaseAuth

import com.despaircorp.domain.firebaseAuth.model.AuthenticateUserEntity
import javax.inject.Inject

class GetAuthenticatedUserUseCase @Inject constructor(
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository,
) {
    suspend fun invoke(): AuthenticateUserEntity =
        firebaseAuthDomainRepository.getCurrentAuthenticatedUser()
}