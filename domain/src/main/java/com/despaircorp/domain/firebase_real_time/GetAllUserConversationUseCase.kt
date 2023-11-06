package com.despaircorp.domain.firebase_real_time

import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firebase_real_time.model.ChatEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAllUserConversationUseCase @Inject constructor(
    private val firebaseRealTimeDomainRepository: FirebaseRealTimeDomainRepository,
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository
) {
    suspend fun invoke(): Flow<List<ChatEntity>> = flow {
        firebaseRealTimeDomainRepository.getChatEntity(
            firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid
        ).collect { chatEntities ->
            emit(
                chatEntities
                    .groupBy { Pair(it.senderUid, it.receiverUid) }
                    .values
                    .map { group ->
                        group.maxByOrNull { it.timestamp } ?: group.first()
                    }
            )
        }
    }
}