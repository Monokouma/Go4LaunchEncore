package com.despaircorp.ui.main.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.despaircorp.domain.location.GetUserLocationEntityAsFlowUseCase
import com.despaircorp.domain.permission.AskForEssentialPermissionUseCase
import com.despaircorp.domain.restaurants.GetNearbyRestaurantsEntityUseCase
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getNearbyRestaurantsEntityUseCase: GetNearbyRestaurantsEntityUseCase,
    private val getUserLocationEntityAsFlowUseCase: GetUserLocationEntityAsFlowUseCase,
    private val hasLocationPermissionUseCase: AskForEssentialPermissionUseCase
) : ViewModel() {
    
    val viewState: LiveData<MapViewState> = liveData {
        getUserLocationEntityAsFlowUseCase.invoke().collect { userLocation ->
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
                    restaurantsCountToast = getNearbyRestaurantsEntityUseCase.invoke(userLocation)
                        .count().let {
                        NativeText.Plural(
                            R.plurals.nearby_restaurants_count,
                            it,
                            listOf(it)
                        )
                    },
                    canShowUserLocation = hasLocationPermissionUseCase.invoke()
                )
            )
        }
        
    }
}