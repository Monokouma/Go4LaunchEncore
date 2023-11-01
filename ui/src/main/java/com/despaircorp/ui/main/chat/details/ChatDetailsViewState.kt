package com.despaircorp.ui.main.chat.details

data class ChatDetailsViewState(
    val receiverName: String,
    val receiverPicture: String,
    val onlineDotResources: Int,
    val sendImageResources: Int
)