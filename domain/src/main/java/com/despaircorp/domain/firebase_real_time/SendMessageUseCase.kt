package com.despaircorp.domain.firebase_real_time

import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firebase_real_time.model.ChatEntity
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val getOrderedUidsUseCase: GetOrderedUidsUseCase,
    private val firebaseRealTimeDomainRepository: FirebaseRealTimeDomainRepository,
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository
) {
    suspend fun invoke(receiverUid: String, message: String): Boolean {
        val currentUserUid = firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid
        val (first, second) = getOrderedUidsUseCase.invoke(receiverUid, currentUserUid)
        
        return firebaseRealTimeDomainRepository.insertMessage(
            firstUid = first,
            secondUid = second,
            chatEntity = ChatEntity(
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