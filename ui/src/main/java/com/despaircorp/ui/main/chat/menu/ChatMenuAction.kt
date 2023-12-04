package com.despaircorp.ui.main.chat.menu

import com.despaircorp.ui.utils.NativeText

sealed class ChatMenuAction {
    data class Error(val error: NativeText) : ChatMenuAction()
    
}