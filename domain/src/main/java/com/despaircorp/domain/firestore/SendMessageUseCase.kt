package com.despaircorp.domain.firestore

import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firebase_real_time.FirebaseRealTimeDomainRepository
import com.despaircorp.domain.firebase_real_time.model.ChatEntity
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val firebaseRealTimeDomainRepository: FirebaseRealTimeDomainRepository,
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository
) {
    suspend fun invoke(receiverUid: String, message: String): Boolean {
        val currentUserUid = firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid
        return firebaseRealTimeDomainRepository.insertMessage(
            ChatEntity(
                chatId = "${currentUserUid}_${
                    LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                }",
                value = message,
                timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
                    .toEpochMilli(),
                senderUid = currentUserUid,
                receiverUid = receiverUid
            )
        )
    }
}