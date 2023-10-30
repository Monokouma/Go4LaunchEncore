package com.despaircorp.data.restaurants

import com.despaircorp.data.retrofit.GooglePlacesApi
import com.despaircorp.data.utils.CoroutineDispatcherProvider
import com.despaircorp.domain.location.model.LocationEntity
import com.despaircorp.domain.restaurants.RestaurantsDomainRepository
import com.despaircorp.domain.restaurants.model.RestaurantEntity
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RestaurantsDataRepository @Inject constructor(
    private val googlePlacesApi: GooglePlacesApi,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    
    ) : RestaurantsDomainRepository {
    override suspend fun getNearbyRestaurants(userLocationEntity: LocationEntity): List<RestaurantEntity> =
        withContext(coroutineDispatcherProvider.io) {
            val dto = googlePlacesApi.getPlaces(
                location = "${userLocationEntity.userLatLng.latitude}, ${userLocationEntity.userLatLng.longitude}",
                radius = 1_000,
                apiKey = "AIzaSyDiJLrfSr0LbHYBplzQvB-IxyWzBDjGAN8",
                type = "restaurant"
            )
            
            dto.results.mapNotNull { result ->
                RestaurantEntity(
                    id = result.placeId ?: return@mapNotNull null,
                    name = result.name ?: return@mapNotNull null,
                    photoUrl = result.photos?.firstOrNull()?.photoReference,
                    latitude = result.geometry?.location?.lat ?: return@mapNotNull null,
                    longitude = result.geometry.location.lng ?: return@mapNotNull null,
                    isOpenedNow = result.openingHours?.openNow == true,
                    workmateInside = 4,
                    vicinity = result.vicinity ?: return@mapNotNull null,
                    rating = result.rating as Double?
                )
            }
            
            
        }
    
}