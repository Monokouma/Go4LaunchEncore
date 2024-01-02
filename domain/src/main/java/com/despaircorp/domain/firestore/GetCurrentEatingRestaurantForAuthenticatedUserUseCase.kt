package com.despaircorp.domain.firestore

import javax.inject.Inject

class GetCurrentEatingRestaurantForAuthenticatedUserUseCase @Inject constructor(
    private val firestoreDomainRepository: FirestoreDomainRepository
) {
    suspend fun invoke(uid: String, placeId: String): Boolean =
        firestoreDomainRepository.getUser(uid).let {
            if (it.currentlyEating) {
                it.eatingPlaceId == placeId
            } else {
                false
            }
        }
}