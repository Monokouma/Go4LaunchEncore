package com.despaircorp.ui.main.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.location.GetUserLocationEntityAsFlowUseCase
import com.despaircorp.domain.location.GetUserLocationEntityUseCase
import com.despaircorp.domain.location.model.LocationEntity
import com.despaircorp.domain.restaurants.GetNearbyRestaurantsEntityUseCase
import com.despaircorp.domain.restaurants.model.RestaurantEntity
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.NativeText
import com.despaircorp.ui.utils.TestCoroutineRule
import com.despaircorp.ui.utils.observeForTesting
import com.google.android.gms.maps.model.LatLng
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MapViewModelUnitTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val getUserLocationEntityUseCase: GetUserLocationEntityUseCase = mockk()
    private val getNearbyRestaurantsEntityUseCase: GetNearbyRestaurantsEntityUseCase = mockk()
    private val getUserLocationEntityAsFlowUseCase: GetUserLocationEntityAsFlowUseCase = mockk()
    
    private lateinit var viewModel: MapViewModel
    
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
        
        private const val DEFAULT_USER_LATITUDE = 10.0
        private const val DEFAULT_USER_LONGITUDE = 20.0
    }
    
    @Before
    fun setup() {
        coEvery { getUserLocationEntityUseCase.invoke() } returns provideLocationEntity()
        coEvery { getNearbyRestaurantsEntityUseCase.invoke(provideLocationEntity()) } returns provideRestaurantsEntity()
        coEvery { getUserLocationEntityAsFlowUseCase.invoke() } returns flowOf(provideLocationEntity())
        
        viewModel = MapViewModel(
            getNearbyRestaurantsEntityUseCase = getNearbyRestaurantsEntityUseCase,
            getUserLocationEntityAsFlowUseCase = getUserLocationEntityAsFlowUseCase,
        )
    }
    
    @Test
    fun `nominal case - view model emit view state with user location and list of nearby restaurants`() =
        testCoroutineRule.runTest {
            viewModel.viewState.observeForTesting(this) {
                assertThat(it.value).isEqualTo(
                    provideMapViewState()
                )
            }
        }
    
    //Region OUT
    
    private fun provideMapViewState() = MapViewState(
        mapViewStateItems = provideMapViewStateItems(),
        userLocation = provideLocationEntity().userLatLng,
        restaurantsCountToast = NativeText.Plural(
            R.plurals.nearby_restaurants_count,
            provideMapViewStateItems().count(),
            listOf(provideMapViewStateItems().count())
        ),
        canShowUserLocation = true
    )
    
    private fun provideMapViewStateItems() = List(3) {
        MapViewStateItems(
            DEFAULT_ID + it,
            DEFAULT_NAME + it,
            DEFAULT_RESTAURANTS_LATITUDE + it,
            DEFAULT_RESTAURANTS_LONGITUDE + it
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
            id = DEFAULT_ID + it,
            name = DEFAULT_NAME + it,
            photoUrl = DEFAULT_PHOTO_URL + it,
            latitude = DEFAULT_RESTAURANTS_LATITUDE + it,
            longitude = DEFAULT_RESTAURANTS_LONGITUDE + it,
            isOpenedNow = DEFAULT_IS_OPENED_NOW,
            workmateInside = DEFAULT_WORKMATE_INSIDE + it,
            vicinity = DEFAULT_VICINITY + it,
            rating = DEFAULT_RATING + it,
            null,
            null
        )
    }
    
    //End Region OUT
}