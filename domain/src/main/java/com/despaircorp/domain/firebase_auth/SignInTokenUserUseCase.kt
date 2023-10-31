package com.despaircorp.domain.firebase_auth

import com.facebook.AccessToken
import javax.inject.Inject

class SignInTokenUserUseCase @Inject constructor(
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository,
    
    ) {
    suspend fun invoke(token: AccessToken): Boolean =
        firebaseAuthDomainRepository.signInTokenUser(token)
}