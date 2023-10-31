package com.despaircorp.domain.firebase_auth

import javax.inject.Inject

class GetCurrentFacebookAccessTokenUseCase @Inject constructor(
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository,
) {
    fun invoke(): String? = firebaseAuthDomainRepository.getCurrentFacebookAccessToken()
}
