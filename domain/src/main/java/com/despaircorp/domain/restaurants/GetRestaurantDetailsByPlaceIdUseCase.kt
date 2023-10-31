package com.despaircorp.domain.restaurants

import com.despaircorp.domain.restaurants.model.RestaurantEntity
import javax.inject.Inject

class GetRestaurantDetailsByPlaceIdUseCase @Inject constructor(
    private val restaurantsDomainRepository: RestaurantsDomainRepository
) {
    suspend fun invoke(placeId: String): RestaurantEntity? =
        restaurantsDomainRepository.getRestaurantByPlaceId(placeId)
}