package com.despaircorp.data.room

import com.despaircorp.data.room.dao.FavoritesRestaurantsDao
import com.despaircorp.data.room.dao.UserPreferencesDao
import com.despaircorp.data.room.entities.FavoritesRestaurantsDataEntity
import com.despaircorp.data.room.entities.UserPreferencesDataEntity
import com.despaircorp.data.utils.CoroutineDispatcherProvider
import com.despaircorp.domain.room.RoomDomainRepository
import com.despaircorp.domain.room.model.FavoritesRestaurantsDomainEntity
import com.despaircorp.domain.room.model.NotificationsStateEnum
import com.despaircorp.domain.room.model.UserPreferencesDomainEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomDataRepository @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val userPreferencesDao: UserPreferencesDao,
    private val favoritesRestaurantsDao: FavoritesRestaurantsDao
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
    
    override suspend fun insertFavoriteRestaurant(favoritesRestaurantsDomainEntity: FavoritesRestaurantsDomainEntity): Long =
        withContext(coroutineDispatcherProvider.io) {
            favoritesRestaurantsDao.insert(
                FavoritesRestaurantsDataEntity(
                    favoritesRestaurantsDomainEntity.placeId
                )
            )
        }
    
    
    override fun getFavoritesRestaurantsEntities(): Flow<List<FavoritesRestaurantsDomainEntity>> =
        favoritesRestaurantsDao.getFavoritesRestaurantsAsFlow().transform { restaurantsEntities ->
            val list = mutableListOf<FavoritesRestaurantsDomainEntity>()
            restaurantsEntities.forEach {
                list.add(
                    FavoritesRestaurantsDomainEntity(
                        it.restaurantPlaceId
                    )
                )
            }
            emit(list)
        }.flowOn(coroutineDispatcherProvider.io)
    
    override suspend fun favoritesTableExist(): Boolean =
        withContext(coroutineDispatcherProvider.io) {
            favoritesRestaurantsDao.exist()
        }
}