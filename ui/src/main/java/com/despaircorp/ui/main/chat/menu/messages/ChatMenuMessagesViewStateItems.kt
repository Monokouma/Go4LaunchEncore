package com.despaircorp.ui.main.chat.menu.messages

data class ChatMenuMessagesViewStateItems(
    val convId: String,
    val senderId: String,
    val receiverId: String,
    val message: String,
    val receiverImage: String,
    val receiverName: String,
    val timestamp: Long
)