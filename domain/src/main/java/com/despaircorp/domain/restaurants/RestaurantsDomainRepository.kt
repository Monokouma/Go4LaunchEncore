package com.despaircorp.domain.restaurants

import com.despaircorp.domain.location.model.LocationEntity
import com.despaircorp.domain.restaurants.model.RestaurantEntity

interface RestaurantsDomainRepository {
    suspend fun getNearbyRestaurants(userLocationEntity: LocationEntity): List<RestaurantEntity>
    suspend fun getRestaurantByPlaceId(placeId: String): RestaurantEntity?
    
}