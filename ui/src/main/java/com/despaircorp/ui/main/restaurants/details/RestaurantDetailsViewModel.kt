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
    savedStateHandle: SavedStateHandle,
    private val getRestaurantDetailsByPlaceIdUseCase: GetRestaurantDetailsByPlaceIdUseCase,
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase,
    private val getCoworkersForSpecificRestaurantUseCase: GetCoworkersForSpecificRestaurantUseCase,
    private val isClickedRestaurantInFavoritesUseCase: IsClickedRestaurantInFavoritesUseCase,
    private val addOrDeleteClickedRestaurantFromFavoriteUseCase: AddOrDeleteClickedRestaurantFromFavoriteUseCase,
    private val isUserEatingInClickedRestaurantUseCase: IsUserEatingInClickedRestaurantUseCase,
    private val removeCurrentEatingRestaurantUseCase: RemoveCurrentEatingRestaurantUseCase,
    private val addCurrentEatingRestaurantUseCase: AddCurrentEatingRestaurantUseCase
) : ViewModel() {
    
    private val placeId = requireNotNull(savedStateHandle.get<String>(ARG_PLACE_ID)) {
        "Please give a Place ID when opening the RestaurantDetails screen!"
    }
    
    val viewState: LiveData<RestaurantDetailsViewState> = liveData {
        val restaurantEntity =
            getRestaurantDetailsByPlaceIdUseCase.invoke(placeId) ?: return@liveData
        val authenticatedUserEntity = getAuthenticatedUserUseCase.invoke()
        
        combine(
            getCoworkersForSpecificRestaurantUseCase.invoke(placeId),
            isClickedRestaurantInFavoritesUseCase.invoke(placeId),
            isUserEatingInClickedRestaurantUseCase.invoke(placeId)
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
                    },
                    onFabClicked = {
                        viewModelScope.launch {
                            if (isUserEatingInClickedRestaurant) {
                                if (!removeCurrentEatingRestaurantUseCase.invoke(
                                        authenticatedUserEntity.uid
                                    )
                                ) {
                                    //Error
                                }
                            } else if (!addCurrentEatingRestaurantUseCase.invoke(
                                    placeId,
                                    uid = authenticatedUserEntity.uid
                                )
                            ) {
                                //Error
                            }
                        }
                    },
                    onLikeClicked = {
                        viewModelScope.launch {
                            if (isClickedRestaurantInFavorite) {
                                if (!addOrDeleteClickedRestaurantFromFavoriteUseCase.invoke(
                                        hadToAddClickedRestaurant = false,
                                        placeId
                                    )
                                ) {
                                    //Error
                                }
                            } else if (!addOrDeleteClickedRestaurantFromFavoriteUseCase.invoke(
                                    hadToAddClickedRestaurant = true,
                                    placeId
                                )
                            ) {
                                
                            }
                        }
                    }
                )
            )
        }.collect()
    }
    
    companion object {
        private const val ARG_PLACE_ID = "ARG_PLACE_ID"
    }
}