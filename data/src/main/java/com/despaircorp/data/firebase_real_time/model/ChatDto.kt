package com.despaircorp.data.firebase_real_time.model

data class ChatDto(
    val chatId: String? = null,
    val value: String? = null,
    val timestamp: Long? = null,
    val senderUid: String? = null,
    val receiverUid: String? = null,
)