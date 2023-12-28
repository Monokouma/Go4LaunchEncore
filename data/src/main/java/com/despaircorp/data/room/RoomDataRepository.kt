package com.despaircorp.data.room

import com.despaircorp.data.room.dao.FavoriteRestaurantDao
import com.despaircorp.data.room.dao.UserPreferencesDao
import com.despaircorp.data.room.entities.FavoriteRestaurantEntity
import com.despaircorp.data.room.entities.UserPreferencesDataEntity
import com.despaircorp.data.utils.CoroutineDispatcherProvider
import com.despaircorp.domain.room.RoomDomainRepository
import com.despaircorp.domain.room.model.ClickedRestaurantsEntity
import com.despaircorp.domain.room.model.NotificationsStateEnum
import com.despaircorp.domain.room.model.UserPreferencesDomainEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomDataRepository @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val userPreferencesDao: UserPreferencesDao,
    private val favoriteRestaurantDao: FavoriteRestaurantDao
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
    
    override suspend fun insertNewClickedRestaurantToFavorite(clickedRestaurantsEntity: ClickedRestaurantsEntity): Long =
        withContext(coroutineDispatcherProvider.io) {
            favoriteRestaurantDao.insert(
                FavoriteRestaurantEntity(
                    clickedRestaurantsEntity.placeId
                )
            )
        }
    
    override suspend fun removeClickedRestaurantToFavorite(clickedRestaurantsEntity: ClickedRestaurantsEntity): Int =
        withContext(coroutineDispatcherProvider.io) {
            favoriteRestaurantDao.remove(clickedRestaurantsEntity.placeId)
        }
    
    override fun getFavoriteRestaurants(): Flow<List<ClickedRestaurantsEntity>> =
        favoriteRestaurantDao.getFavoriteRestaurantsAsFlow().transform { favoritesRestaurants ->
            val list = mutableListOf<ClickedRestaurantsEntity>()
            favoritesRestaurants.forEach {
                list.add(
                    ClickedRestaurantsEntity(it.placeId)
                )
            }
            emit(list)
        }
    
}