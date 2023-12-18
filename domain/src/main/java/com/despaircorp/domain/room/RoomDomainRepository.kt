package com.despaircorp.domain.room

import com.despaircorp.domain.room.model.FavoritesRestaurantsDomainEntity
import com.despaircorp.domain.room.model.NotificationsStateEnum
import com.despaircorp.domain.room.model.UserPreferencesDomainEntity
import kotlinx.coroutines.flow.Flow

interface RoomDomainRepository {
    suspend fun isNotificationEnabled(): UserPreferencesDomainEntity
    suspend fun insertUserPreferences(userPreferencesDomainEntity: UserPreferencesDomainEntity): Long
    suspend fun exist(): Boolean
    suspend fun updateNotificationPreferences(notificationState: NotificationsStateEnum): Int
    suspend fun insertFavoriteRestaurant(favoritesRestaurantsDomainEntity: FavoritesRestaurantsDomainEntity): Long
    fun getFavoritesRestaurantsEntities(): Flow<List<FavoritesRestaurantsDomainEntity>>
    suspend fun favoritesTableExist(): Boolean
}