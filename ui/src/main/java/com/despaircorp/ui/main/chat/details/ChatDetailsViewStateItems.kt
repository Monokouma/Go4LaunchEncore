package com.despaircorp.ui.main.chat.details

data class ChatDetailsViewStateItems(
    val receiverId: String,
    val senderId: String,
    val messageValue: String,
    val timestamp: Long,
    val isOnRight: Boolean
)