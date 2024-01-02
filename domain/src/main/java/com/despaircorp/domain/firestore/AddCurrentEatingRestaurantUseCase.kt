package com.despaircorp.domain.firestore

import javax.inject.Inject

class AddCurrentEatingRestaurantUseCase @Inject constructor(
    private val firestoreDomainRepository: FirestoreDomainRepository
) {
    suspend fun invoke(placeId: String, uid: String): Boolean =
        firestoreDomainRepository.addCurrentEatingRestaurant(placeId, uid)
}