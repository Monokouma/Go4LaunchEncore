package com.despaircorp.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites_restaurants_table")
data class FavoritesRestaurantsDataEntity(
    @ColumnInfo(name = "restaurantPlaceId") val restaurantPlaceId: String,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
)