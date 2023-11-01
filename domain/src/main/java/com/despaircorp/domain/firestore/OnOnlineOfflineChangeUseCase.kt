package com.despaircorp.domain.firestore

import javax.inject.Inject

class OnOnlineOfflineChangeUseCase @Inject constructor(
    private val firestoreDomainRepository: FirestoreDomainRepository
) {
    suspend fun invoke(state: Boolean, uid: String) =
        firestoreDomainRepository.updateUserOnlineState(
            isOnline = state,
            uid = uid
        )
}