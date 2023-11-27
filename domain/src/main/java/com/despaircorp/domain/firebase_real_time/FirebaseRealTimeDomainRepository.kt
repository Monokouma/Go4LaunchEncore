package com.despaircorp.domain.firebase_real_time

import com.despaircorp.domain.firebase_real_time.model.ChatEntity
import kotlinx.coroutines.flow.Flow


interface FirebaseRealTimeDomainRepository {
    fun getAllLastChatEntities(senderUid: String): Flow<List<ChatEntity>>
    fun createConversation(firstUid: String, secondUid: String): Flow<Boolean>
    suspend fun insertMessage(firstUid: String, secondUid: String, chatEntity: ChatEntity): Boolean
    fun getMessagesBetweenCurrentUserAndSpecificUser(
        currentUserUid: String,
        specificUserUid: String
    ): Flow<List<ChatEntity>>
    
}