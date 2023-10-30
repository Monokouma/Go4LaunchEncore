package com.despaircorp.domain.location

import com.despaircorp.domain.location.model.LocationEntity

interface LocationDomainRepository {
    suspend fun getUserLocation(): LocationEntity
    suspend fun getDistanceBetweenPlaceAndUser(
        userLocation: LocationEntity,
        restaurantLat: Double,
        restaurantLong: Double
    ): Int
}