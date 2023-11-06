package com.despaircorp.ui.main.chat.menu

interface ChatMenuListener {
    fun onUserClicked(uid: String)
    
    fun onConversationClicked(conversationId: String)
}