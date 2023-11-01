package com.despaircorp.domain.firebase_real_time

import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateConversationUseCase @Inject constructor(
    private val firebaseRealTimeDomainRepository: FirebaseRealTimeDomainRepository,
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository
) {
    suspend fun invoke(receiverUid: String): Flow<Boolean> = flow {
        firebaseRealTimeDomainRepository.getAllConversation(
            receiverUid,
            firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid
        ).collect { chatsEntities ->
            
            chatsEntities.forEach { chatEntity ->
                
                /*
                if (chatEntity.receiver == receiverUid && chatEntity.sender == firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid) {
                    Log.i("Monokouma", chatEntity.toString())
                } else {
                    firebaseRealTimeDomainRepository.createConversation(
                        receiverUid,
                        firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid
                    )
                    return@collect
                }
                
                 */
            }
        }
    }
    
}
    