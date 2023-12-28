package com.despaircorp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.despaircorp.data.room.entities.FavoriteRestaurantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteRestaurantDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favoriteRestaurantEntity: FavoriteRestaurantEntity): Long
    
    @Query("SELECT * FROM favorite_restaurant_table")
    fun getFavoriteRestaurantsAsFlow(): Flow<List<FavoriteRestaurantEntity>>
    
    @Query("DELETE FROM favorite_restaurant_table WHERE placeId=:placeId")
    suspend fun remove(placeId: String): Int
}