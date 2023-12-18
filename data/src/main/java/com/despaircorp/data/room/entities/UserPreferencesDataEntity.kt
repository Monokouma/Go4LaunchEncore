package com.despaircorp.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.despaircorp.domain.room.model.NotificationsStateEnum

@Entity(tableName = "user_preferences_table")
data class UserPreferencesDataEntity(
    @PrimaryKey(autoGenerate = false)
    val isNotificationEnable: NotificationsStateEnum,
)

