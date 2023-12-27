package com.despaircorp.domain.room

import com.despaircorp.domain.room.model.NotificationsStateEnum
import com.despaircorp.domain.room.model.UserPreferencesDomainEntity

interface RoomDomainRepository {
    suspend fun isNotificationEnabled(): UserPreferencesDomainEntity
    suspend fun insertUserPreferences(userPreferencesDomainEntity: UserPreferencesDomainEntity): Long
    suspend fun exist(): Boolean
    suspend fun updateNotificationPreferences(notificationState: NotificationsStateEnum): Int
    
}