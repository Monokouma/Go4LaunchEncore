package com.despaircorp.domain.firebase_real_time.model

data class ChatEntity(
    val chatId: String,
    val value: String,
    val timestamp: Long,
    val senderUid: String,
    val receiverUid: String,
)