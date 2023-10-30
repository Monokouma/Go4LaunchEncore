package com.despaircorp.domain.location

import com.despaircorp.domain.location.model.LocationEntity
import javax.inject.Inject

class GetDistanceBetweenUserAndPlacesUseCase @Inject constructor(
    private val locationDomainRepository: LocationDomainRepository
) {
    suspend fun invoke(
        userLocation: LocationEntity,
        restaurantLat: Double,
        restaurantLong: Double,
    ): Int = locationDomainRepository.getDistanceBetweenPlaceAndUser(
        userLocation,
        restaurantLat,
        restaurantLong,
    )
}