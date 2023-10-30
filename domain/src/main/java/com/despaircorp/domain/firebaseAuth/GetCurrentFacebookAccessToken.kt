package com.despaircorp.domain.firebaseAuth

import javax.inject.Inject

class GetCurrentFacebookAccessToken @Inject constructor(
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository,
) {
    fun invoke(): String? = firebaseAuthDomainRepository.getCurrentFacebookAccessToken()
}
