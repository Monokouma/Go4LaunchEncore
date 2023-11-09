package com.despaircorp.ui.main.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.despaircorp.domain.location.GetUserLocationEntityUseCase
import com.despaircorp.domain.restaurants.GetNearbyRestaurantsEntityUseCase
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getUserLocationEntityUseCase: GetUserLocationEntityUseCase,
    private val getNearbyRestaurantsEntityUseCase: GetNearbyRestaurantsEntityUseCase,
    
    ) : ViewModel() {
    
    val viewState = liveData {
        val userLocation = getUserLocationEntityUseCase.invoke()
        val restaurantCount = getNearbyRestaurantsEntityUseCase.invoke(userLocation).count()
        emit(
            MapViewState(
                mapViewStateItems = getNearbyRestaurantsEntityUseCase.invoke(userLocation).map {
                    MapViewStateItems(
                        placeId = it.id,
                        name = it.name,
                        latitude = it.latitude,
                        longitude = it.longitude,
                    )
                },
                userLocation = userLocation.userLatLng,
                restaurantsCountToast = NativeText.Plural(
                    R.plurals.nearby_restaurants_count,
                    restaurantCount,
                    listOf(restaurantCount)
                )
            )
        )
    }
}