package com.despaircorp.ui.main.restaurants.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.despaircorp.domain.firebase_auth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firestore.AddCurrentEatingRestaurantUseCase
import com.despaircorp.domain.firestore.GetCoworkersForSpecificRestaurantUseCase
import com.despaircorp.domain.firestore.IsUserEatingInClickedRestaurantUseCase
import com.despaircorp.domain.firestore.RemoveCurrentEatingRestaurantUseCase
import com.despaircorp.domain.restaurants.GetRestaurantDetailsByPlaceIdUseCase
import com.despaircorp.domain.room.AddOrDeleteClickedRestaurantFromFavoriteUseCase
import com.despaircorp.domain.room.IsClickedRestaurantInFavoritesUseCase
import com.despaircorp.ui.BuildConfig
import com.despaircorp.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getRestaurantDetailsByPlaceIdUseCase: GetRestaurantDetailsByPlaceIdUseCase,
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase,
    private val getCoworkersForSpecificRestaurantUseCase: GetCoworkersForSpecificRestaurantUseCase,
    private val isClickedRestaurantInFavoritesUseCase: IsClickedRestaurantInFavoritesUseCase,
    private val addOrDeleteClickedRestaurantFromFavoriteUseCase: AddOrDeleteClickedRestaurantFromFavoriteUseCase,
    private val isUserEatingInClickedRestaurantUseCase: IsUserEatingInClickedRestaurantUseCase,
    private val removeCurrentEatingRestaurantUseCase: RemoveCurrentEatingRestaurantUseCase,
    private val addCurrentEatingRestaurantUseCase: AddCurrentEatingRestaurantUseCase
) : ViewModel() {
    
    val viewState: LiveData<RestaurantDetailsViewState> = liveData {
        
        val restaurantEntity = getRestaurantDetailsByPlaceIdUseCase.invoke(
            savedStateHandle.get<String>(ARG_PLACE_ID) ?: return@liveData
        ) ?: return@liveData
        combine(
            getCoworkersForSpecificRestaurantUseCase.invoke(
                placeId = savedStateHandle.get<String>(ARG_PLACE_ID) ?: return@liveData
            ),
            isClickedRestaurantInFavoritesUseCase.invoke(
                placeId = savedStateHandle.get<String>(ARG_PLACE_ID) ?: return@liveData
            ),
            isUserEatingInClickedRestaurantUseCase.invoke(
                uid = getAuthenticatedUserUseCase.invoke().uid,
                placeId = savedStateHandle.get<String>(ARG_PLACE_ID) ?: return@liveData
            )
        ) { coworkersEntities, isClickedRestaurantInFavorite, isUserEatingInClickedRestaurant ->
            
            emit(
                RestaurantDetailsViewState(
                    name = restaurantEntity.name,
                    rating = restaurantEntity.rating,
                    photoUrl = StringBuilder()
                        .append("https://maps.googleapis.com/maps/api/place/photo?maxwidth=1920&maxheigth=1080&photo_reference=")
                        .append(restaurantEntity.photoUrl)
                        .append("&key=${BuildConfig.MAPS_API_KEY}")
                        .toString(),
                    vicinity = restaurantEntity.vicinity,
                    websiteUrl = restaurantEntity.webSiteUrl,
                    phoneNumber = restaurantEntity.phoneNumber,
                    coworkersInside = coworkersEntities.map {
                        RestaurantDetailsCoworkerViewStateItems(
                            id = it.uid,
                            picture = it.pictureUrl,
                            coworkerName = it.name,
                        )
                    },
                    isSnackBarVisible = false,
                    snackBarMessage = null,
                    snackBarColor = R.color.rusty_red,
                    fabIcon = if (isUserEatingInClickedRestaurant) {
                        R.drawable.check_mark
                    } else {
                        R.drawable.cursor
                    },
                    likeIcon = if (isClickedRestaurantInFavorite) {
                        R.drawable.star__2_
                    } else {
                        R.drawable.star__1_
                    }
                )
            )
        }.collect()
        
        
    }
    
    fun onEatButtonClicked() {
        viewModelScope.launch {
            isUserEatingInClickedRestaurantUseCase.invoke(
                uid = getAuthenticatedUserUseCase.invoke().uid,
                placeId = savedStateHandle.get<String>(ARG_PLACE_ID) ?: return@launch
            ).collect {
                if (it) {
                    if (removeCurrentEatingRestaurantUseCase.invoke(getAuthenticatedUserUseCase.invoke().uid)) {
                        //Success
                    } else {
                        //Error
                    }
                } else {
                    if (addCurrentEatingRestaurantUseCase.invoke(
                            placeId = savedStateHandle.get<String>(
                                ARG_PLACE_ID
                            ) ?: return@collect, uid = getAuthenticatedUserUseCase.invoke().uid
                        )
                    ) {
                        //Success
                    } else {
                        //Error
                    }
                }
            }
        }
    }
    
    fun onFavButtonClicked(placeId: String) {
        viewModelScope.launch {
            isClickedRestaurantInFavoritesUseCase.invoke(placeId).collect {
                if (it) {
                    
                    if (addOrDeleteClickedRestaurantFromFavoriteUseCase.invoke(
                            hadToAddClickedRestaurant = false,
                            placeId
                        )
                    ) {
                        //Success
                    } else {
                        //error
                    }
                } else {
                    if (addOrDeleteClickedRestaurantFromFavoriteUseCase.invoke(
                            hadToAddClickedRestaurant = true,
                            placeId
                        )
                    ) {
                        //Success
                    } else {
                        //error
                    }
                }
            }
        }
    }
    
    
    companion object {
        private const val ARG_PLACE_ID = "ARG_PLACE_ID"
    }
}