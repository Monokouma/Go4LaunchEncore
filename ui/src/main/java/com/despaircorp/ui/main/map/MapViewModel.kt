package com.despaircorp.ui.main.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.despaircorp.domain.location.GetUserLocationEntityUseCase
import com.despaircorp.domain.restaurants.GetNearbyRestaurantsEntityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getUserLocationEntityUseCase: GetUserLocationEntityUseCase,
    private val getNearbyRestaurantsEntityUseCase: GetNearbyRestaurantsEntityUseCase,
    
    ) : ViewModel() {
    
    val viewState = liveData<MapViewState> {
        val userLocation = getUserLocationEntityUseCase.invoke()
        
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
                getNearbyRestaurantsEntityUseCase.invoke(userLocation).count()
            )
        )
    }
}