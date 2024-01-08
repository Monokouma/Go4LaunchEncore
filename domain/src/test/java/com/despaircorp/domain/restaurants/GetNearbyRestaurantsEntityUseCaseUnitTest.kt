package com.despaircorp.domain.restaurants

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.location.model.LocationEntity
import com.despaircorp.domain.restaurants.model.RestaurantEntity
import com.despaircorp.domain.utils.TestCoroutineRule
import com.google.android.gms.maps.model.LatLng
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetNearbyRestaurantsEntityUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val restaurantsDomainRepository: RestaurantsDomainRepository = mockk()
    
    private val useCase: GetNearbyRestaurantsEntityUseCase = GetNearbyRestaurantsEntityUseCase(
        restaurantsDomainRepository = restaurantsDomainRepository
    )
    
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
        coEvery { restaurantsDomainRepository.getNearbyRestaurants(provideLocationEntity()) } returns provideRestaurantsEntity()
        
    }
    
    @Test
    fun `nominal case - get restaurants entity`() = testCoroutineRule.runTest {
        val result = useCase.invoke(provideLocationEntity())
        
        assertThat(result).isEqualTo(
            provideRestaurantsEntity()
        )
        
        coVerify {
            restaurantsDomainRepository.getNearbyRestaurants(provideLocationEntity())
        }
        
        confirmVerified(restaurantsDomainRepository)
    }
    
    //Region OUT
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
}