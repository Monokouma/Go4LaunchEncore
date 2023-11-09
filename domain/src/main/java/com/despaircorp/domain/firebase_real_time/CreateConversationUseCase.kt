package com.despaircorp.domain.firebase_real_time

import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateConversationUseCase @Inject constructor(
    private val getOrderedUidsUseCase: GetOrderedUidsUseCase,
    private val firebaseRealTimeDomainRepository: FirebaseRealTimeDomainRepository,
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository
) {
    suspend fun invoke(receiverUid: String): Flow<Boolean> {
        val (first, second) = getOrderedUidsUseCase.invoke(receiverUid, firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid)

        return firebaseRealTimeDomainRepository.createConversation(
            first,
            second
        )
    }
}
    