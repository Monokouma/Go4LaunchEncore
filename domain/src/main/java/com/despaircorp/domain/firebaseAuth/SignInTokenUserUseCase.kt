package com.despaircorp.domain.firebaseAuth

import com.facebook.AccessToken
import javax.inject.Inject

class SignInTokenUserUseCase @Inject constructor(
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository,
    
    ) {
    suspend fun invoke(token: AccessToken) = firebaseAuthDomainRepository.signInTokenUser(token)
}