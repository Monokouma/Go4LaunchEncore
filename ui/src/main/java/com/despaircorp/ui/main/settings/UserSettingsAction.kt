package com.despaircorp.ui.main.settings

sealed class UserSettingsAction {
    object ModificationsSuccess : UserSettingsAction()
    data class Error(val message: Int) : UserSettingsAction()
}