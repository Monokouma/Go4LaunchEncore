package com.despaircorp.domain.firestore

import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firestore.model.CoworkersEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class GetAllCoworkersUseCase @Inject constructor(
    private val firestoreDomainRepository: FirestoreDomainRepository,
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository
) {
    fun invoke(): Flow<List<CoworkersEntity>> =
        firestoreDomainRepository.getAllFirestoreUsers().mapLatest { firestoreUserEntities ->
            val currentUser = firebaseAuthDomainRepository.getCurrentAuthenticatedUser()
            val coworkerEntities = mutableListOf<CoworkersEntity>()
            
            firestoreUserEntities.forEach { firestoreUserEntity ->
                
                if (firestoreUserEntity.uid != currentUser.uid) {
                    coworkerEntities.add(
                        CoworkersEntity(
                            uid = firestoreUserEntity.uid,
                            eatingPlaceId = if (firestoreUserEntity.currentlyEating) {
                                firestoreUserEntity.eatingPlaceId ?: return@forEach
                            } else {
                                null
                            },
                            isEating = firestoreUserEntity.currentlyEating,
                            pictureUrl = firestoreUserEntity.picture,
                            name = firestoreUserEntity.displayName
                        )
                    )
                }
            }
            
            coworkerEntities
        }
}