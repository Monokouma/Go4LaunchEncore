package com.despaircorp.domain.restaurants

import com.despaircorp.domain.location.model.LocationEntity
import com.despaircorp.domain.restaurants.model.RestaurantEntity
import javax.inject.Inject

class GetNearbyRestaurantsEntityUseCase @Inject constructor(
    private val restaurantsDomainRepository: RestaurantsDomainRepository

) {
    
    suspend fun invoke(userLocationEntity: LocationEntity): List<RestaurantEntity> =
        restaurantsDomainRepository.getNearbyRestaurants(userLocationEntity)
}