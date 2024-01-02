package com.despaircorp.ui.main.restaurants.list

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.despaircorp.domain.firestore.GetCoworkersForSpecificRestaurantAsFlowUseCase
import com.despaircorp.domain.location.GetDistanceBetweenUserAndPlacesUseCase
import com.despaircorp.domain.location.GetUserLocationEntityUseCase
import com.despaircorp.domain.restaurants.GetNearbyRestaurantsEntityUseCase
import com.despaircorp.ui.BuildConfig
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class RestaurantsViewModel @Inject constructor(
    private val getUserLocationEntityUseCase: GetUserLocationEntityUseCase,
    private val getNearbyRestaurantsEntityUseCase: GetNearbyRestaurantsEntityUseCase,
    private val getDistanceBetweenUserAndPlacesUseCase: GetDistanceBetweenUserAndPlacesUseCase,
    private val getCoworkersForSpecificRestaurantAsFlowUseCase: GetCoworkersForSpecificRestaurantAsFlowUseCase,
    private val application: Application
) : ViewModel() {
    
    val viewState = liveData<RestaurantsViewState> {
        if (latestValue == null) {
            emit(
                RestaurantsViewState(
                    restaurants = emptyList(),
                    isSpinnerVisible = true
                )
            )
        }
        
        emit(
            RestaurantsViewState(
                restaurants = getNearbyRestaurantsEntityUseCase.invoke(getUserLocationEntityUseCase.invoke())
                    .map { nearbyRestaurant ->
                        val distance = getDistanceBetweenUserAndPlacesUseCase.invoke(
                            userLocation = getUserLocationEntityUseCase.invoke(),
                            restaurantLat = nearbyRestaurant.latitude,
                            restaurantLong = nearbyRestaurant.longitude,
                        )
                        
                        val coworkers =
                            getCoworkersForSpecificRestaurantAsFlowUseCase.invoke(nearbyRestaurant.id)
                                .first()
                        
                        val isOpenNow = nearbyRestaurant.isOpenedNow
                        val pictureUrl = StringBuilder()
                            .append("https://maps.googleapis.com/maps/api/place/photo?maxwidth=1920&maxheigth=1080&photo_reference=")
                            .append(nearbyRestaurant.photoUrl)
                            .append("&key=${BuildConfig.MAPS_API_KEY}")
                            .toString()
                        
                        RestaurantsViewStateItems(
                            restaurantName = nearbyRestaurant.name,
                            restaurantDistance = "${distance}m",
                            restaurantImageUrl = pictureUrl,
                            restaurantAddressAndType = nearbyRestaurant.vicinity,
                            workmatesInside = if (coworkers.isNotEmpty()) {
                                "${coworkers.count()}"
                            } else {
                                application.getString(R.string.zero)
                            },
                            restaurantSchedule = if (isOpenNow) {
                                NativeText.Resource(R.string.opened)
                            } else {
                                NativeText.Resource(R.string.closed)
                            },
                            restaurantStar = nearbyRestaurant.rating,
                            openedTextColorRes = if (isOpenNow) {
                                R.color.shamrock_green
                            } else {
                                R.color.rusty_red
                            },
                            placeId = nearbyRestaurant.id
                        )
                    },
                isSpinnerVisible = false,
            )
        )
    }
}