package com.despaircorp.domain.restaurants

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.restaurants.model.RestaurantEntity
import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetRestaurantDetailsByPlaceIdUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val restaurantsDomainRepository: RestaurantsDomainRepository = mockk()
    
    private val useCase = GetRestaurantDetailsByPlaceIdUseCase(
        restaurantsDomainRepository = restaurantsDomainRepository
    )
    
    companion object {
        private const val DEFAULT_ID = "DEFAULT_ID"
        private const val DEFAULT_NAME = "DEFAULT_NAME"
        private const val DEFAULT_PHOTO_URL = "DEFAULT_PHOTO_URL"
        private const val DEFAULT_RESTAURANTS_LATITUDE = 10.0
        private const val DEFAULT_RESTAURANTS_LONGITUDE = 20.0
        private const val DEFAULT_IS_OPENED_NOW = true
        private const val DEFAULT_WORKMATE_INSIDE = 0
        private const val DEFAULT_VICINITY = "DEFAULT_VICINITY"
        private const val DEFAULT_RATING = 3.0
        private const val DEFAULT_WEBSITE_URL = "DEFAULT_WEBSITE_URL"
        private const val DEFAULT_PHONE_NUMBER = "DEFAULT_PHONE_NUMBER"
    }
    
    @Before
    fun setup() {
        coEvery { restaurantsDomainRepository.getRestaurantByPlaceId(DEFAULT_ID) } returns provideRestaurantsEntity()
        
    }
    
    @Test
    fun `nominal case - get restaurant by id`() = testCoroutineRule.runTest {
        val result = useCase.invoke(DEFAULT_ID)
        
        assertThat(result).isEqualTo(provideRestaurantsEntity())
        
        coVerify {
            restaurantsDomainRepository.getRestaurantByPlaceId(DEFAULT_ID)
        }
        confirmVerified(restaurantsDomainRepository)
    }
    
    private fun provideRestaurantsEntity() = RestaurantEntity(
        id = DEFAULT_ID,
        name = DEFAULT_NAME,
        photoUrl = DEFAULT_PHOTO_URL,
        latitude = DEFAULT_RESTAURANTS_LATITUDE,
        longitude = DEFAULT_RESTAURANTS_LONGITUDE,
        isOpenedNow = DEFAULT_IS_OPENED_NOW,
        workmateInside = DEFAULT_WORKMATE_INSIDE,
        vicinity = DEFAULT_VICINITY,
        rating = DEFAULT_RATING,
        DEFAULT_WEBSITE_URL,
        DEFAULT_PHONE_NUMBER
    )
    
}