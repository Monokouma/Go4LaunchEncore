package com.despaircorp.domain.firestore

import android.util.Log
import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firestore.model.CoworkersEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCoworkersForSpecificRestaurantAsFlowUseCase @Inject constructor(
    private val firestoreDomainRepository: FirestoreDomainRepository,
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository
) {
    fun invoke(placeId: String): Flow<List<CoworkersEntity>> = flow {
        val currentUser =
            firestoreDomainRepository.getUser(firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid)
        
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
            
            list.remove(
                CoworkersEntity(
                    currentUser.uid,
                    currentUser.currentlyEating,
                    currentUser.eatingPlaceId,
                    currentUser.picture,
                    currentUser.displayName
                )
            )
            Log.i("Monokouma", list.toString())
            emit(list)
        }
    }
}