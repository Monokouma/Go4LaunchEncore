package com.despaircorp.ui.main.settings

data class UserSettingsViewState(
    val emailAddress: String,
    val displayName: String,
    val isNotificationsChecked: Boolean,
    val userImageUrl: String
)