package com.despaircorp.ui.main.chat.menu

import com.despaircorp.ui.main.chat.menu.messages.ChatMenuMessagesViewStateItems
import com.despaircorp.ui.main.chat.menu.online_users.ChatMenuOnlineUserViewStateItems

data class ChatMenuViewState(
    val chatMenuOnlineUserViewStateItems: List<ChatMenuOnlineUserViewStateItems>,
    val chatMessagesViewStateItems: List<ChatMenuMessagesViewStateItems>
)