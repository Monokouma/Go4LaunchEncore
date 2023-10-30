package com.despaircorp.domain.firestore

import com.despaircorp.domain.firestore.model.FirestoreUserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFirestoreUserAsFlowUseCase @Inject constructor(
    private val firestoreDomainRepository: FirestoreDomainRepository
) {
    fun invoke(uid: String): Flow<FirestoreUserEntity> =
        firestoreDomainRepository.getUserAsFlow(uid)
}