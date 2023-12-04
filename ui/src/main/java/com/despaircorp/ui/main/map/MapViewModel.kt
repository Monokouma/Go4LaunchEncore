package com.despaircorp.ui.main.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.despaircorp.domain.location.GetUserLocationEntityAsFlowUseCase
import com.despaircorp.domain.restaurants.GetNearbyRestaurantsEntityUseCase
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getNearbyRestaurantsEntityUseCase: GetNearbyRestaurantsEntityUseCase,
    private val getUserLocationEntityAsFlowUseCase: GetUserLocationEntityAsFlowUseCase,
) : ViewModel() {
    
    val viewState: LiveData<MapViewState> = liveData {
        getUserLocationEntityAsFlowUseCase.invoke().collect { userLocation ->
            val restaurants = getNearbyRestaurantsEntityUseCase.invoke(userLocation)
            emit(
                MapViewState(
                    mapViewStateItems = restaurants.map {
                        MapViewStateItems(
                            placeId = it.id,
                            name = it.name,
                            latitude = it.latitude,
                            longitude = it.longitude,
                        )
                    },
                    userLocation = userLocation.userLatLng,
                    restaurantsCountToast = restaurants.count().let {
                        NativeText.Plural(
                            R.plurals.nearby_restaurants_count,
                            it,
                            listOf(it)
                        )
                    },
                    canShowUserLocation = true
                )
            )
        }
        
    }
}