package com.despaircorp.domain.firebase_auth

import com.despaircorp.domain.firebase_auth.model.AuthenticateUserEntity
import javax.inject.Inject

class GetAuthenticatedUserUseCase @Inject constructor(
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository,
) {
    suspend fun invoke(): AuthenticateUserEntity =
        firebaseAuthDomainRepository.getCurrentAuthenticatedUser()
}