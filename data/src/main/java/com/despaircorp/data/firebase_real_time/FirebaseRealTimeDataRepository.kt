package com.despaircorp.data.firebase_real_time

import com.despaircorp.data.firebase_real_time.model.ChatDto
import com.despaircorp.data.utils.CoroutineDispatcherProvider
import com.despaircorp.domain.firebase_real_time.FirebaseRealTimeDomainRepository
import com.despaircorp.domain.firebase_real_time.model.ChatEntity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseRealTimeDataRepository @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val firebaseRealTime: FirebaseDatabase,
    
    ) : FirebaseRealTimeDomainRepository {
    override suspend fun getChatEntity(
        senderUid: String
    ): Flow<List<ChatEntity>> = callbackFlow {
        val registration =
            firebaseRealTime.getReference("chat")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatsDto = mutableListOf<ChatDto>()
                
                snapshot.children.forEach { children ->
                    if (children.key?.contains(senderUid) == true) {
                        val receiver = children.child("receiver").value as? String
                        
                        children.children.forEach { messageSnapshot ->
                            if (messageSnapshot.key != "sender" && messageSnapshot.key != "receiver") {
                                chatsDto.add(
                                    ChatDto(
                                        chatId = messageSnapshot.key,
                                        value = messageSnapshot.child("value").value as? String,
                                        timestamp = messageSnapshot.child("timestamp").value as? Long,
                                        senderUid = messageSnapshot.child("senderUID").value as? String,
                                        receiverUid = receiver
                                    )
                                )
                            }
                        }
                    }
                }
                
                trySend(
                    chatsDto.map { chatDto ->
                        ChatEntity(
                            chatId = chatDto.chatId ?: return,
                            value = chatDto.value ?: return,
                            timestamp = chatDto.timestamp ?: return,
                            senderUid = chatDto.senderUid ?: return,
                            receiverUid = chatDto.receiverUid ?: return
                        )
                    }.sortedByDescending { it.timestamp }
                )
                
                
            }
            
            override fun onCancelled(error: DatabaseError) {}
        }
        
        registration.addValueEventListener(listener)
        
        awaitClose { registration.removeEventListener(listener) }
    }.flowOn(coroutineDispatcherProvider.io)
    
    override fun createConversation(receiverUid: String, uid: String): Flow<Boolean> =
        callbackFlow<Boolean> {
            val registration =
                firebaseRealTime.getReference("chat").child("${uid}_${receiverUid}")
            
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        trySend(true)
                    } else {
                        registration.setValue(
                            mapOf(
                                "receiver" to receiverUid,
                                "sender" to uid
                            )
                        )
                    }
                }
                
                override fun onCancelled(error: DatabaseError) {}
            }
            
            registration.addValueEventListener(listener)
            
            awaitClose { registration.removeEventListener(listener) }
        }.flowOn(coroutineDispatcherProvider.io)
    
    override suspend fun insertMessage(chatEntity: ChatEntity): Boolean =
        withContext(coroutineDispatcherProvider.io) {
            try {
                firebaseRealTime.getReference("chat")
                    .child("${chatEntity.senderUid}_${chatEntity.receiverUid}")
                    .child(chatEntity.chatId)
                    .setValue(
                        mapOf(
                            "senderUID" to chatEntity.value,
                            "timestamp" to chatEntity.timestamp,
                            "value" to chatEntity.value
                        )
                    ).await()
                true
            } catch (e: Exception) {
                coroutineContext.ensureActive()
                false
            }
        }
    
}