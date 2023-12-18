package com.despaircorp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.despaircorp.data.room.entities.FavoritesRestaurantsDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesRestaurantsDao {
    @Query("SELECT EXISTS(SELECT * FROM favorites_restaurants_table)")
    fun exist(): Boolean
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoritesRestaurantsDataEntity: FavoritesRestaurantsDataEntity): Long
    
    @Query("SELECT * FROM favorites_restaurants_table")
    fun getFavoritesRestaurantsAsFlow(): Flow<List<FavoritesRestaurantsDataEntity>>
    
    @Query("DELETE FROM favorites_restaurants_table WHERE restaurantPlaceId=:placeId")
    suspend fun deleteFavoriteRestaurantByPlaceId(placeId: String): Int
}