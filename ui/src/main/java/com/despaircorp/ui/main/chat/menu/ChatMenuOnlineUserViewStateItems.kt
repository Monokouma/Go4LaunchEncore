package com.despaircorp.ui.main.chat.menu

data class ChatMenuOnlineUserViewStateItems(
    val uid: String,
    val name: String,
    val dotDrawable: Int,
    val userImageAlpha: Float,
    val pictureUrl: String,
    val online: Boolean
)