package com.despaircorp.data.firebase_real_time

import com.despaircorp.data.firebase_real_time.model.ChatsDto
import com.despaircorp.data.utils.CoroutineDispatcherProvider
import com.despaircorp.domain.firebase_real_time.FirebaseRealTimeDomainRepository
import com.despaircorp.domain.firebase_real_time.model.ChatsEntity
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
    override suspend fun getAllConversation(
        receiverUid: String,
        senderUid: String
    ): Flow<List<ChatsEntity>> = callbackFlow {
        val registration = firebaseRealTime.getReference("chat")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatsDto = mutableListOf<ChatsDto>()
                val chatsEntity = mutableListOf<ChatsEntity>()
                
                snapshot.children.forEach { snapshotChildren ->
                    chatsDto.add(
                        ChatsDto(
                            id = snapshotChildren.key ?: return@forEach,
                            receiver = snapshotChildren.child("receiver").value as String,
                            sender = snapshotChildren.child("receiver").value as String,
                        )
                    )
                }
                
                chatsDto.forEach { chatDto ->
                    chatsEntity.add(
                        ChatsEntity(
                            id = chatDto.id ?: return@forEach,
                            receiver = chatDto.receiver ?: return@forEach,
                            sender = chatDto.sender ?: return@forEach
                        )
                    )
                }
                
                trySend(chatsEntity)
            }
            
            override fun onCancelled(error: DatabaseError) {}
        }
        
        registration.addListenerForSingleValueEvent(listener)
        
        awaitClose { registration.removeEventListener(listener) }
    }.flowOn(coroutineDispatcherProvider.io)
    
    override suspend fun createConversation(receiverUid: String, senderUid: String): Boolean =
        withContext(coroutineDispatcherProvider.io) {
            try {
                firebaseRealTime.getReference("chat").push().setValue(
                    mapOf(
                        "receiver" to receiverUid,
                        "sender" to senderUid
                    )
                ).await()
                true
            } catch (e: Exception) {
                coroutineContext.ensureActive()
                e.printStackTrace()
                false
            }
            
        }
    
}