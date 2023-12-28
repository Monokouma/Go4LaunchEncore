package com.despaircorp.domain.firestore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class IsUserEatingInClickedRestaurantUseCase @Inject constructor(
    private val firestoreDomainRepository: FirestoreDomainRepository
) {
    suspend fun invoke(uid: String, placeId: String): Flow<Boolean> =
        firestoreDomainRepository.getCurrentEatingRestaurantForAuthenticatedUser(uid).transform {
            emit(
                if (placeId == it) {
                    true
                } else if (it == null) {
                    false
                } else {
                    false
                }
            )
        }
}