package com.despaircorp.domain.firestore

import com.despaircorp.domain.firebase_auth.GetAuthenticatedUserUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IsUserEatingInClickedRestaurantUseCase @Inject constructor(
    private val firestoreDomainRepository: FirestoreDomainRepository,
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase
) {
    fun invoke(placeId: String): Flow<Boolean> = flow {
        firestoreDomainRepository.getCurrentEatingRestaurantForAuthenticatedUser(getAuthenticatedUserUseCase.invoke().uid).map {
            placeId == it
        }.collect(this)
    }
}