package com.despaircorp.ui.main.restaurants.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.location.GetDistanceBetweenUserAndPlacesUseCase
import com.despaircorp.domain.location.GetUserLocationEntityUseCase
import com.despaircorp.domain.location.model.LocationEntity
import com.despaircorp.domain.restaurants.GetNearbyRestaurantsEntityUseCase
import com.despaircorp.domain.restaurants.model.RestaurantEntity
import com.despaircorp.ui.BuildConfig
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.NativeText
import com.despaircorp.ui.utils.TestCoroutineRule
import com.despaircorp.ui.utils.observeForTesting
import com.google.android.gms.maps.model.LatLng
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RestaurantsViewModelUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private val getUserLocationEntityUseCase: GetUserLocationEntityUseCase = mockk()
    private val getNearbyRestaurantsEntityUseCase: GetNearbyRestaurantsEntityUseCase = mockk()
    private val getDistanceBetweenUserAndPlacesUseCase: GetDistanceBetweenUserAndPlacesUseCase =
        mockk()
    
    private lateinit var viewModel: RestaurantsViewModel
    
    companion object {
        private const val DEFAULT_ID = "DEFAULT_ID"
        private const val DEFAULT_NAME = "DEFAULT_NAME"
        private const val DEFAULT_PHOTO_URL = "DEFAULT_PHOTO_URL"
        private const val DEFAULT_RESTAURANTS_LATITUDE = 10.0
        private const val DEFAULT_RESTAURANTS_LONGITUDE = 20.0
        private const val DEFAULT_IS_OPENED_NOW = true
        private const val DEFAULT_WORKMATE_INSIDE = 2
        private const val DEFAULT_VICINITY = "DEFAULT_VICINITY"
        private const val DEFAULT_RATING = 3.0
        
        private const val DEFAULT_DISTANCE_BETWEEN_USER_AND_RESTAURANT = 100
        private const val DEFAULT_USER_LATITUDE = 10.0
        private const val DEFAULT_USER_LONGITUDE = 20.0
    }
    
    @Before
    fun setup() {
        coEvery {
            getDistanceBetweenUserAndPlacesUseCase.invoke(
                provideLocationEntity(),
                DEFAULT_RESTAURANTS_LATITUDE,
                DEFAULT_RESTAURANTS_LONGITUDE
            )
        } returns DEFAULT_DISTANCE_BETWEEN_USER_AND_RESTAURANT
        
        coEvery { getUserLocationEntityUseCase.invoke() } returns provideLocationEntity()
        coEvery { getNearbyRestaurantsEntityUseCase.invoke(provideLocationEntity()) } returns provideRestaurantsEntity()
        
        
        viewModel = RestaurantsViewModel(
            getUserLocationEntityUseCase = getUserLocationEntityUseCase,
            getNearbyRestaurantsEntityUseCase = getNearbyRestaurantsEntityUseCase,
            getDistanceBetweenUserAndPlacesUseCase = getDistanceBetweenUserAndPlacesUseCase
        )
    }
    
    @Test
    fun `nominal case - get list of nearby restaurants`() = testCoroutineRule.runTest {
        viewModel.viewState.observeForTesting(this) {
            assertThat(it.value).isEqualTo(
                provideRestaurantsViewState()
            )
        }
    }
    
    @Test
    fun `edge case - closed restaurants`() = testCoroutineRule.runTest {
        coEvery { getNearbyRestaurantsEntityUseCase.invoke(provideLocationEntity()) } returns provideClosedRestaurantsEntity()
        viewModel.viewState.observeForTesting(this) {
            assertThat(it.value).isEqualTo(
                provideRestaurantsViewState().copy(
                    restaurants = provideClosedRestaurantsViewStateItems()
                )
            )
        }
    }
    
    
    //Region OUT
    private fun provideRestaurantsViewState() = RestaurantsViewState(
        restaurants = provideRestaurantsViewStateItems(),
        isSpinnerVisible = false
    )
    
    private fun provideRestaurantsViewStateItems() = List(3) {
        RestaurantsViewStateItems(
            restaurantName = DEFAULT_NAME,
            restaurantDistance = "${DEFAULT_DISTANCE_BETWEEN_USER_AND_RESTAURANT}m",
            restaurantImageUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1920&maxheigth=1080&photo_reference=$DEFAULT_PHOTO_URL&key=${BuildConfig.MAPS_API_KEY}",
            restaurantAddressAndType = DEFAULT_VICINITY,
            workmatesInside = "$DEFAULT_WORKMATE_INSIDE",
            restaurantSchedule = NativeText.Resource(R.string.opened),
            restaurantStar = DEFAULT_RATING,
            openedTextColorRes = R.color.shamrock_green,
            placeId = DEFAULT_ID
        )
    }
    
    private fun provideLocationEntity() = LocationEntity(
        userLatLng = LatLng(
            DEFAULT_USER_LATITUDE,
            DEFAULT_USER_LONGITUDE
        )
    )
    
    private fun provideRestaurantsEntity() = List(3) {
        RestaurantEntity(
            id = DEFAULT_ID,
            name = DEFAULT_NAME,
            photoUrl = DEFAULT_PHOTO_URL,
            latitude = DEFAULT_RESTAURANTS_LATITUDE,
            longitude = DEFAULT_RESTAURANTS_LONGITUDE,
            isOpenedNow = DEFAULT_IS_OPENED_NOW,
            workmateInside = DEFAULT_WORKMATE_INSIDE,
            vicinity = DEFAULT_VICINITY,
            rating = DEFAULT_RATING,
        )
    }
    
    private fun provideClosedRestaurantsEntity() = List(3) {
        RestaurantEntity(
            id = DEFAULT_ID,
            name = DEFAULT_NAME,
            photoUrl = DEFAULT_PHOTO_URL,
            latitude = DEFAULT_RESTAURANTS_LATITUDE,
            longitude = DEFAULT_RESTAURANTS_LONGITUDE,
            isOpenedNow = false,
            workmateInside = DEFAULT_WORKMATE_INSIDE,
            vicinity = DEFAULT_VICINITY,
            rating = DEFAULT_RATING,
        )
    }
    
    private fun provideClosedRestaurantsViewStateItems() = List(3) {
        RestaurantsViewStateItems(
            restaurantName = DEFAULT_NAME,
            restaurantDistance = "${DEFAULT_DISTANCE_BETWEEN_USER_AND_RESTAURANT}m",
            restaurantImageUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1920&maxheigth=1080&photo_reference=$DEFAULT_PHOTO_URL&key=${BuildConfig.MAPS_API_KEY}",
            restaurantAddressAndType = DEFAULT_VICINITY,
            workmatesInside = "$DEFAULT_WORKMATE_INSIDE",
            restaurantSchedule = NativeText.Resource(R.string.closed),
            restaurantStar = DEFAULT_RATING,
            openedTextColorRes = R.color.rusty_red,
            placeId = DEFAULT_ID
        )
    }
}