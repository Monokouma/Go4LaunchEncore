package com.despaircorp.domain.firebase_auth

import javax.inject.Inject

class UpdateAuthMailAddressUseCase @Inject constructor(
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository
) {
    suspend fun invoke(newMailAddress: String): Boolean =
        firebaseAuthDomainRepository.updateMailAddress(newMailAddress)
}