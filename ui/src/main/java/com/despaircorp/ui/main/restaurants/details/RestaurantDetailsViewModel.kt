package com.despaircorp.ui.main.restaurants.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.despaircorp.domain.firebase_auth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firestore.GetCoworkersForSpecificRestaurantUseCase
import com.despaircorp.domain.firestore.GetCurrentEatingRestaurantForAuthenticatedUserUseCase
import com.despaircorp.domain.firestore.UpdateCurrentEatingRestaurantUseCase
import com.despaircorp.domain.restaurants.GetRestaurantDetailsByPlaceIdUseCase
import com.despaircorp.domain.room.AddRestaurantsToFavoritesUseCase
import com.despaircorp.domain.room.IsCurrentRestaurantsInFavoriteUseCase
import com.despaircorp.ui.BuildConfig
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getRestaurantDetailsByPlaceIdUseCase: GetRestaurantDetailsByPlaceIdUseCase,
    private val updateCurrentEatingRestaurantUseCase: UpdateCurrentEatingRestaurantUseCase,
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase,
    private val getCurrentEatingRestaurantForAuthenticatedUserUseCase: GetCurrentEatingRestaurantForAuthenticatedUserUseCase,
    private val getCoworkersForSpecificRestaurantUseCase: GetCoworkersForSpecificRestaurantUseCase,
    private val addRestaurantsToFavoritesUseCase: AddRestaurantsToFavoritesUseCase,
    private val isCurrentRestaurantsInFavoriteUseCase: IsCurrentRestaurantsInFavoriteUseCase
) : ViewModel() {
    
    private val viewStateMutableLiveData: MutableLiveData<RestaurantDetailsViewState> =
        MutableLiveData()
    val viewState: LiveData<RestaurantDetailsViewState> = viewStateMutableLiveData
    
    init {
        viewModelScope.launch {
            
            val coworkerList = getCoworkersForSpecificRestaurantUseCase.invoke(
                savedStateHandle.get<String>(ARG_PLACE_ID) ?: return@launch
            ).first()
            
            val isRestaurantInFav = isCurrentRestaurantsInFavoriteUseCase.invoke(
                savedStateHandle.get<String>(ARG_PLACE_ID) ?: return@launch
            ).first()
            
            val restaurantEntity = getRestaurantDetailsByPlaceIdUseCase.invoke(
                savedStateHandle.get<String>(ARG_PLACE_ID) ?: return@launch
            ) ?: return@launch
            
            viewStateMutableLiveData.value = RestaurantDetailsViewState(
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
                coworkersInside = coworkerList.map {
                    RestaurantDetailsCoworkerViewStateItems(
                        id = it.uid,
                        picture = it.pictureUrl,
                        coworkerName = it.name,
                    )
                },
                isSnackBarVisible = false,
                snackBarMessage = null,
                snackBarColor = R.color.rusty_red,
                fabIcon = if (getCurrentEatingRestaurantForAuthenticatedUserUseCase.invoke(
                        getAuthenticatedUserUseCase.invoke().uid,
                        savedStateHandle.get<String>(ARG_PLACE_ID) ?: return@launch
                    )
                ) {
                    R.drawable.check_mark
                } else {
                    R.drawable.cursor
                },
                if (isRestaurantInFav) {
                    R.drawable.star__2_
                } else {
                    R.drawable.star__1_
                }
            )
        }
    }
    
    
    fun onEatButtonClicked() {
        viewModelScope.launch {
            if (updateCurrentEatingRestaurantUseCase.invoke(
                    savedStateHandle.get<String>(
                        ARG_PLACE_ID
                    ) ?: return@launch, getAuthenticatedUserUseCase.invoke().uid
                )
            ) {
                
                viewStateMutableLiveData.value = viewState.value?.copy(
                    isSnackBarVisible = true,
                    snackBarMessage = NativeText.Resource(R.string.saved_restaurant),
                    snackBarColor = R.color.shamrock_green,
                    fabIcon = R.drawable.check_mark
                )
            } else {
                viewStateMutableLiveData.value = viewState.value?.copy(
                    isSnackBarVisible = true,
                    snackBarMessage = NativeText.Resource(R.string.error_occurred),
                    snackBarColor = R.color.rusty_red,
                    fabIcon = R.drawable.cursor
                )
            }
        }
    }
    
    fun onFavoritesButtonClicked() {
        viewModelScope.launch {
            isCurrentRestaurantsInFavoriteUseCase.invoke(
                savedStateHandle.get<String>(
                    ARG_PLACE_ID
                ) ?: return@launch
            ).collect {
                
                Log.i("Monokouma", it.toString())
                
                if (it) {
                    viewStateMutableLiveData.value = viewState.value?.copy(
                        likeIcon = R.drawable.star__1_,
                    )
                } else {
                    if (addRestaurantsToFavoritesUseCase.invoke(
                            savedStateHandle.get<String>(
                                ARG_PLACE_ID
                            ) ?: return@collect
                        )
                    ) {
                        viewStateMutableLiveData.value = viewState.value?.copy(
                            likeIcon = R.drawable.star__2_,
                        )
                    } else {
                        viewStateMutableLiveData.value = viewState.value?.copy(
                            isSnackBarVisible = true,
                            snackBarMessage = NativeText.Resource(R.string.error_occurred),
                            snackBarColor = R.color.rusty_red,
                        )
                    }
                    
                }
            }
            
        }
    }
    
    companion object {
        private const val ARG_PLACE_ID = "ARG_PLACE_ID"
    }
}