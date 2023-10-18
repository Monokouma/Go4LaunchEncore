package com.despaircorp.ui.username

sealed class ChoseUsernameAction {
    data class Error(val message: Int) : ChoseUsernameAction()
    
    object Continue : ChoseUsernameAction()
    
    object EnableNotifications : ChoseUsernameAction()
    
}