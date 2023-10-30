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

class GetUserLocationEntityUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val locationDomainRepository: LocationDomainRepository = mockk()
    
    private val useCase: GetUserLocationEntityUseCase = GetUserLocationEntityUseCase(
        locationDomainRepository
    )
    
    companion object {
        private const val DEFAULT_USER_LATITUDE = 10.0
        private const val DEFAULT_USER_LONGITUDE = 20.0
    }
    
    @Before
    fun setup() {
        coEvery { locationDomainRepository.getUserLocation() } returns provideLocationEntity()
    }
    
    @Test
    fun `nominal case - get location entity`() = testCoroutineRule.runTest {
        val result = useCase.invoke()
        
        assertThat(result).isEqualTo(
            provideLocationEntity()
        )
        
        coVerify {
            locationDomainRepository.getUserLocation()
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