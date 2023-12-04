package com.despaircorp.domain.location

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.location.model.LocationEntity
import com.despaircorp.domain.utils.TestCoroutineRule
import com.google.android.gms.maps.model.LatLng
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetUserLocationEntityAsFlowUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val locationDomainRepository: LocationDomainRepository = mockk()
    
    private val useCase = GetUserLocationEntityAsFlowUseCase(
        locationDomainRepository
    )
    
    companion object {
        private const val DEFAULT_USER_LATITUDE = 10.0
        private const val DEFAULT_USER_LONGITUDE = 20.0
    }
    
    @Before
    fun setup() {
        coEvery { locationDomainRepository.getUserLocationAsFlow() } returns flowOf(
            provideLocationEntity()
        )
    }
    
    @Test
    fun `nominal case - get user location as flow`() = testCoroutineRule.runTest {
        useCase.invoke().test {
            val result = awaitItem()
            awaitComplete()
            
            assertThat(result).isEqualTo(provideLocationEntity())
            
            coVerify {
                locationDomainRepository.getUserLocationAsFlow()
            }
            
            confirmVerified(locationDomainRepository)
        }
    }
    
    private fun provideLocationEntity() = LocationEntity(
        LatLng(DEFAULT_USER_LATITUDE, DEFAULT_USER_LONGITUDE)
    )
}