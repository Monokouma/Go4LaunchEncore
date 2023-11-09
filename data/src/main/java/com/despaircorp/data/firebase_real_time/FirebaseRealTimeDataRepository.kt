package com.despaircorp.data.firebase_real_time

import com.despaircorp.data.utils.CoroutineDispatcherProvider
import com.despaircorp.domain.firebase_real_time.FirebaseRealTimeDomainRepository
import com.despaircorp.domain.firebase_real_time.model.ChatEntity
import com.despaircorp.domain.firebase_real_time.model.ConversationParticipantsEntity
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
//    fun getChatEntity(
//        senderUid: String,
//        receiverUid: String,
//    ): Flow<List<ChatEntity>>

    override fun getAllLastChatEntities(
        senderUid: String
    ): Flow<List<ChatEntity>> = callbackFlow {
        val registration = firebaseRealTime.getReference("chat")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(
                    snapshot.children.mapNotNull { conversation ->
                        val receiver = conversation.child("receiver").value as? String ?: return@mapNotNull null

                        if (conversation.key?.contains(senderUid) == true) {
                            conversation.children
                                .asSequence()
                                .filter { it.key != "sender" && it.key != "receiver" }
                                .mapNotNull { messageSnapshot ->
                                    mapToChatEntities(messageSnapshot, receiver)
                                }
                                .maxByOrNull { it.timestamp }
                        } else {
                            null
                        }
                    }
                )
            }

            override fun onCancelled(error: DatabaseError) {}
        }

        registration.addValueEventListener(listener)

        awaitClose { registration.removeEventListener(listener) }
    }.flowOn(coroutineDispatcherProvider.io)

    override fun createConversation(firstUid: String, secondUid: String): Flow<Boolean> =
        callbackFlow<Boolean> {
            val registration =
                firebaseRealTime.getReference("chat").child("${firstUid}_${secondUid}")

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        trySend(true)
                    } else {
                        registration.setValue(
                            mapOf(
                                "receiver" to firstUid,
                                "sender" to secondUid
                            )
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            }

            registration.addValueEventListener(listener)

            awaitClose { registration.removeEventListener(listener) }
        }.flowOn(coroutineDispatcherProvider.io)

    override suspend fun insertMessage(firstUid: String, secondUid: String, chatEntity: ChatEntity): Boolean =
        withContext(coroutineDispatcherProvider.io) {
            try {
                firebaseRealTime.getReference("chat")
                    .child("${firstUid}_${secondUid}")
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

    private fun mapToChatEntities(
        messageSnapshot: DataSnapshot,
        receiver: String
    ): ChatEntity? {
        return ChatEntity(
            chatId = messageSnapshot.key ?: return null,
            value = messageSnapshot.child("value").value as? String ?: return null,
            timestamp = messageSnapshot.child("timestamp").value as? Long ?: return null,
            senderUid = messageSnapshot.child("senderUID").value as? String ?: return null,
            receiverUid = receiver
        )
    }
}