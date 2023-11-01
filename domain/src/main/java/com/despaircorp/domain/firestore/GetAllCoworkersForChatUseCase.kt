package com.despaircorp.domain.firestore

import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firestore.model.CoworkersChatEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class GetAllCoworkersForChatUseCase @Inject constructor(
    private val firestoreDomainRepository: FirestoreDomainRepository,
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository
) {
    fun invoke(): Flow<List<CoworkersChatEntity>> =
        firestoreDomainRepository.getAllFirestoreUsers().mapLatest { firestoreUserEntities ->
            val currentUser = firebaseAuthDomainRepository.getCurrentAuthenticatedUser()
            val coworkerEntities = mutableListOf<CoworkersChatEntity>()
            
            firestoreUserEntities.forEach { firestoreUserEntity ->
                
                if (firestoreUserEntity.uid != currentUser.uid) {
                    coworkerEntities.add(
                        CoworkersChatEntity(
                            uid = firestoreUserEntity.uid,
                            pictureUrl = firestoreUserEntity.picture,
                            name = firestoreUserEntity.displayName,
                            isOnline = firestoreUserEntity.online
                        )
                    )
                }
            }
            
            coworkerEntities
        }
}