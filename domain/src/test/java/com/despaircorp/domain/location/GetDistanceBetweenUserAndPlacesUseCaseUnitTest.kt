package com.despaircorp.domain.location

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.location.model.LocationEntity
import com.despaircorp.domain.utils.TestCoroutineRule
import com.google.android.gms.maps.model.LatLng
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetDistanceBetweenUserAndPlacesUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val locationDomainRepository: LocationDomainRepository = mockk()
    
    private val useCase = GetDistanceBetweenUserAndPlacesUseCase(
        locationDomainRepository
    )
    
    companion object {
        private const val DEFAULT_USER_LATITUDE = 10.0
        private const val DEFAULT_USER_LONGITUDE = 20.0
        
        private const val DEFAULT_RESTAURANT_LATITUDE = 10.0
        private const val DEFAULT_RESTAURANT_LONGITUDE = 20.0
        
        private const val DEFAULT_DISTANCE = 100
    }
    
    @Before
    fun setup() {
        coEvery {
            locationDomainRepository.getDistanceBetweenPlaceAndUser(
                provideLocationEntity(),
                DEFAULT_RESTAURANT_LATITUDE,
                DEFAULT_RESTAURANT_LONGITUDE,
            )
        } returns DEFAULT_DISTANCE
    }
    
    @Test
    fun `nominal case - get correct distance between restaurant and user`() =
        testCoroutineRule.runTest {
            val result = useCase.invoke(
                provideLocationEntity(),
                DEFAULT_RESTAURANT_LATITUDE,
                DEFAULT_RESTAURANT_LONGITUDE
            )
            
            assertThat(result).isEqualTo(DEFAULT_DISTANCE)
            
            coVerify {
                locationDomainRepository.getDistanceBetweenPlaceAndUser(
                    provideLocationEntity(),
                    DEFAULT_RESTAURANT_LATITUDE,
                    DEFAULT_RESTAURANT_LONGITUDE,
                )
            }
            
            confirmVerified(locationDomainRepository)
        }
    
    private fun provideLocationEntity() = LocationEntity(
        userLatLng = LatLng(
            DEFAULT_USER_LATITUDE,
            DEFAULT_USER_LONGITUDE
        )
    )
}