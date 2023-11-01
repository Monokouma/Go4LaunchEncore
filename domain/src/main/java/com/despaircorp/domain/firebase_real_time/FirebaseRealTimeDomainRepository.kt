package com.despaircorp.domain.firebase_real_time

import com.despaircorp.domain.firebase_real_time.model.ChatsEntity
import kotlinx.coroutines.flow.Flow


interface FirebaseRealTimeDomainRepository {
    suspend fun getAllConversation(
        receiverUid: String,
        senderUid: String
    ): Flow<List<ChatsEntity>>
    
    suspend fun createConversation(receiverUid: String, senderUid: String): Boolean
    
}