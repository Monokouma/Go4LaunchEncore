package com.despaircorp.data.restaurants

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.data.restaurants.dto.details.Geometry
import com.despaircorp.data.restaurants.dto.details.PhotosItem
import com.despaircorp.data.restaurants.dto.list.Location
import com.despaircorp.data.restaurants.dto.list.OpeningHours
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
        private const val DEFAULT_WORKMATE_INSIDE = 4
        private const val DEFAULT_VICINITY = "DEFAULT_VICINITY"
        private const val DEFAULT_RATING = 3.0
        
        private const val DEFAULT_LATITUDE = 48.857920
        private const val DEFAULT_LONGITUDE = 2.295048
        
        private const val DEFAULT_RADIUS = 1_000
        private const val DEFAULT_PLACES_TYPE = "restaurant"
        
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
            provideRestaurantsEntity()
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
    
    //Region OUT
    private fun provideRestaurantsEntity() = List(1) {
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
    
    private fun provideGeometry() = Geometry(
        location = getDefaultLocation()
    )
    
    private fun provideOpeningHour() = OpeningHours(
        openNow = DEFAULT_IS_OPENED_NOW
    )
    
    private fun getDefaultLocation() = Location(
        lat = DEFAULT_RESTAURANTS_LATITUDE,
        lng = DEFAULT_RESTAURANTS_LONGITUDE,
    )
    
    private fun providePhotoItem(): List<PhotosItem> = List(size = 1) {
        PhotosItem(
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