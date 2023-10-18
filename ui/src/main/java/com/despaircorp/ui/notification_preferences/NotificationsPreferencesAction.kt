package com.despaircorp.ui.notification_preferences

sealed class NotificationsPreferencesAction {
    object Success : NotificationsPreferencesAction()
    
    data class Error(val message: Int) : NotificationsPreferencesAction()
}