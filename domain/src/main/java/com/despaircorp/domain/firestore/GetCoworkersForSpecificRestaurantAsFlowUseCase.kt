package com.despaircorp.domain.firestore

import com.despaircorp.domain.firestore.model.CoworkersEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCoworkersForSpecificRestaurantAsFlowUseCase @Inject constructor(
    private val firestoreDomainRepository: FirestoreDomainRepository
) {
    fun invoke(placeId: String): Flow<List<CoworkersEntity>> = flow {
        firestoreDomainRepository.getAllFirestoreUsersAsFlow().collect { userEntities ->
            val list = mutableListOf<CoworkersEntity>()
            userEntities.forEach {
                if (it.currentlyEating) {
                    if (it.eatingPlaceId == placeId) {
                        list.add(
                            CoworkersEntity(
                                uid = it.uid,
                                isEating = it.currentlyEating,
                                eatingPlaceId = it.eatingPlaceId,
                                pictureUrl = it.picture,
                                name = it.displayName
                            )
                        )
                    } else {
                        return@forEach
                    }
                } else {
                    return@forEach
                }
            }
            emit(list)
        }
    }
}