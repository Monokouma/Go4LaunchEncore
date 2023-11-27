package com.despaircorp.domain.firebase_real_time

import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firebase_real_time.model.ChatEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllUserMessagesWithSpecificUserUseCase @Inject constructor(
    private val firebaseRealTimeDomainRepository: FirebaseRealTimeDomainRepository,
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository
) {
    suspend fun invoke(specificUserUid: String): Flow<List<ChatEntity>> =
        firebaseRealTimeDomainRepository.getMessagesBetweenCurrentUserAndSpecificUser(
            firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid,
            specificUserUid
        )
}