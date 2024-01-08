package com.despaircorp.data.restaurants

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.data.restaurants.dto.details.Location
import com.despaircorp.data.restaurants.dto.details.RestaurantDetailsDto
import com.despaircorp.data.restaurants.dto.details.Result
import com.despaircorp.data.restaurants.dto.list.Geometry
import com.despaircorp.data.restaurants.dto.list.OpeningHours
import com.despaircorp.data.restaurants.dto.list.PhotosItem
import com.despaircorp.data.restaurants.dto.list.RestaurantsDto
import com.despaircorp.data.restaurants.dto.list.ResultsItem
import com.despaircorp.data.retrofit.GooglePlacesApi
import com.despaircorp.data.utils.TestCoroutineRule
import com.despaircorp.domain.location.model.LocationEntity
import com.despaircorp.domain.restaurants.model.RestaurantEntity
import com.google.android.gms.maps.model.LatLng
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RestaurantsDataRepositoryUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val googlePlacesApi: GooglePlacesApi = mockk()
    
    private val repository = RestaurantsDataRepository(
        googlePlacesApi = googlePlacesApi,
        coroutineDispatcherProvider = testCoroutineRule.getTestCoroutineDispatcherProvider()
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
        
        private const val DEFAULT_LATITUDE = 48.857920
        private const val DEFAULT_LONGITUDE = 2.295048
        
        private const val DEFAULT_RADIUS = 1_000
        private const val DEFAULT_PLACES_TYPE = "restaurant"
        
        private const val DEFAULT_PLACE_ID = "DEFAULT_PLACE_ID"
        private const val DEFAULT_WEBSITE_URL = "DEFAULT_WEBSITE_URL"
        private const val DEFAULT_PHONE_NUMBER = "DEFAULT_PHONE_NUMBER"
    }
    
    @Before
    fun setup() {
        coEvery {
            googlePlacesApi.getPlaces(
                location = "$DEFAULT_LATITUDE, $DEFAULT_LONGITUDE",
                radius = DEFAULT_RADIUS,
                apiKey = any(),
                type = DEFAULT_PLACES_TYPE
            )
        } returns provideDefaultRestaurantsDto()
    }
    
    @Test
    fun `nominal case - get restaurants entity`() = testCoroutineRule.runTest {
        val result = repository.getNearbyRestaurants(provideLocationEntity())
        assertThat(result).isEqualTo(
            provideRestaurantsEntityAsList()
        )
        
        coVerify {
            googlePlacesApi.getPlaces(
                location = "$DEFAULT_LATITUDE, $DEFAULT_LONGITUDE",
                radius = DEFAULT_RADIUS,
                apiKey = any(),
                type = DEFAULT_PLACES_TYPE
            )
        }
        
        confirmVerified(googlePlacesApi)
    }
    
    @Test
    fun `nominal case - get restaurants details by place id`() = testCoroutineRule.runTest {
        coEvery {
            googlePlacesApi.getPlacesDetails(
                any(),
                DEFAULT_PLACE_ID
            )
        } returns provideRestaurantDetailsDto()
        
        val result = repository.getRestaurantByPlaceId(DEFAULT_PLACE_ID)
        
        assertThat(result).isEqualTo(
            provideRestaurantsEntity().copy(
                id = DEFAULT_PLACE_ID,
                webSiteUrl = DEFAULT_WEBSITE_URL,
                phoneNumber = DEFAULT_PHONE_NUMBER,
                workmateInside = 4
            )
        )
        
        coVerify {
            googlePlacesApi.getPlacesDetails(
                any(),
                DEFAULT_PLACE_ID
            )
        }
        
        confirmVerified(googlePlacesApi)
    }
    
    //Region OUT
    
    private fun provideRestaurantDetailsDto() = RestaurantDetailsDto(
        result = Result(
            placeId = DEFAULT_PLACE_ID,
            name = DEFAULT_NAME,
            rating = DEFAULT_RATING,
            vicinity = DEFAULT_VICINITY,
            website = DEFAULT_WEBSITE_URL,
            formattedPhoneNumber = DEFAULT_PHONE_NUMBER,
            photos = listOf(
                PhotosItem(
                    photoReference = DEFAULT_PHOTO_URL
                )
            ),
            geometry = Geometry(
                location = Location(
                    lat = DEFAULT_RESTAURANTS_LATITUDE,
                    lng = DEFAULT_RESTAURANTS_LONGITUDE
                )
            )
        )
    )
    
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
        null,
        null
    )
    
    
    private fun provideRestaurantsEntityAsList() = List(1) {
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
            null,
            null
        )
    }
    
    private fun provideDefaultRestaurantsDto() = RestaurantsDto(
        results = listOf(
            ResultsItem(
                placeId = DEFAULT_ID,
                name = DEFAULT_NAME,
                photos = providePhotoItem(),
                geometry = provideGeometry(),
                openingHours = provideOpeningHour(),
                rating = DEFAULT_RATING,
                vicinity = DEFAULT_VICINITY,
            )
        )
    )
    
    private fun provideGeometry() = com.despaircorp.data.restaurants.dto.details.Geometry(
        location = getDefaultLocation()
    )
    
    private fun provideOpeningHour() = OpeningHours(
        openNow = DEFAULT_IS_OPENED_NOW
    )
    
    private fun getDefaultLocation() = com.despaircorp.data.restaurants.dto.list.Location(
        lat = DEFAULT_RESTAURANTS_LATITUDE,
        lng = DEFAULT_RESTAURANTS_LONGITUDE,
    )
    
    private fun providePhotoItem(): List<com.despaircorp.data.restaurants.dto.details.PhotosItem> =
        List(size = 1) {
            com.despaircorp.data.restaurants.dto.details.PhotosItem(
                photoReference = DEFAULT_PHOTO_URL
            )
        }
    
    private fun provideLocationEntity() = LocationEntity(
        userLatLng = LatLng(
            DEFAULT_LATITUDE,
            DEFAULT_LONGITUDE
        )
    )
    //end Region OUT
}