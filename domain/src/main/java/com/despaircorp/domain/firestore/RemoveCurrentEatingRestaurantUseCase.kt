package com.despaircorp.domain.firestore

import javax.inject.Inject

class RemoveCurrentEatingRestaurantUseCase @Inject constructor(
    private val firestoreDomainRepository: FirestoreDomainRepository
) {
    suspend fun invoke(uid: String): Boolean =
        firestoreDomainRepository.removeCurrentEatingRestaurant(uid)
}