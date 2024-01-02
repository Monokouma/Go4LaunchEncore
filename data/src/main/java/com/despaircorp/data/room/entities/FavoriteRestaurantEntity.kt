package com.despaircorp.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_restaurant_table")
data class FavoriteRestaurantEntity(
    @PrimaryKey(autoGenerate = false)
    val placeId: String,
)