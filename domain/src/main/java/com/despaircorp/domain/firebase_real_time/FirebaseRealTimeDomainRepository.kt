package com.despaircorp.domain.firebase_real_time

import com.despaircorp.domain.firebase_real_time.model.ChatEntity
import kotlinx.coroutines.flow.Flow


interface FirebaseRealTimeDomainRepository {
    suspend fun getChatEntity(
        senderUid: String
    ): Flow<List<ChatEntity>>
    
    fun createConversation(receiverUid: String, uid: String): Flow<Boolean>
    suspend fun insertMessage(chatEntity: ChatEntity): Boolean
    
}