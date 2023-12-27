package com.despaircorp.data.room

import com.despaircorp.data.room.dao.UserPreferencesDao
import com.despaircorp.data.room.entities.UserPreferencesDataEntity
import com.despaircorp.data.utils.CoroutineDispatcherProvider
import com.despaircorp.domain.room.RoomDomainRepository
import com.despaircorp.domain.room.model.NotificationsStateEnum
import com.despaircorp.domain.room.model.UserPreferencesDomainEntity
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomDataRepository @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val userPreferencesDao: UserPreferencesDao,
) : RoomDomainRepository {
    override suspend fun isNotificationEnabled(): UserPreferencesDomainEntity =
        withContext(coroutineDispatcherProvider.io) {
            UserPreferencesDomainEntity(
                userPreferencesDao.getUserPreferences().isNotificationEnable
            )
        }
    
    override suspend fun insertUserPreferences(userPreferencesDomainEntity: UserPreferencesDomainEntity) =
        withContext(coroutineDispatcherProvider.io) {
            userPreferencesDao.insertUserPreferencesEntity(
                UserPreferencesDataEntity(
                    userPreferencesDomainEntity.isNotificationsEnabled,
                )
            )
        }
    
    override suspend fun exist(): Boolean = withContext(coroutineDispatcherProvider.io) {
        userPreferencesDao.exist()
    }
    
    override suspend fun updateNotificationPreferences(notificationState: NotificationsStateEnum): Int =
        withContext(coroutineDispatcherProvider.io) {
            userPreferencesDao.updateUserPreferencesNotification(notificationState)
        }
    
}