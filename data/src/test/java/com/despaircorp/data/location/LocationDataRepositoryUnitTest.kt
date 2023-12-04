package com.despaircorp.data.location

import android.location.Location
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.data.utils.TestCoroutineRule
import com.despaircorp.domain.location.model.LocationEntity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runCurrent
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LocationDataRepositoryUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val fusedLocationProviderClient: FusedLocationProviderClient = mockk()
    
    private lateinit var repository: LocationDataRepository
    
    companion object {
        private const val DEFAULT_LATITUDE = 48.857920
        private const val DEFAULT_LONGITUDE = 2.295048
    }
    
    @Before
    fun setup() {
        repository = LocationDataRepository(
            fusedLocationProviderClient = fusedLocationProviderClient,
            coroutineDispatcherProvider = testCoroutineRule.getTestCoroutineDispatcherProvider()
        )
    }
    
    
    @Test
    fun `nominal case - get user location`() = testCoroutineRule.runTest {
        val task: Task<Location> = mockk()
        val slot = slot<OnCompleteListener<Location>>()
        
        every { task.isComplete } returns true
        every { task.isSuccessful } returns true
        every { task.isCanceled } returns false
        every { task.exception } returns null
        every { task.result.latitude } returns DEFAULT_LATITUDE
        every { task.result.longitude } returns DEFAULT_LONGITUDE
        
        every {
            fusedLocationProviderClient.lastLocation
        } answers {
            every { task.addOnCompleteListener(capture(slot)) } answers {
                slot.captured.onComplete(task)
                task
            }
            task
        }
        
        val result = repository.getUserLocation()
        
        assertThat(result).isEqualTo(
            provideLocationEntity()
        )
        
        coVerify {
            fusedLocationProviderClient.lastLocation
        }
        
        confirmVerified(fusedLocationProviderClient)
    }
    
    @Test
    fun `nominal test - get distance between place and user`() = testCoroutineRule.runTest {
        
        val result = repository.getDistanceBetweenPlaceAndUser(
            provideLocationEntity().copy(userLatLng = LatLng(49.857920, 1.295048)),
            DEFAULT_LATITUDE,
            DEFAULT_LONGITUDE
        )
        
        assertThat(result).isEqualTo(0)
        
    }
    
    @Test
    fun `nominal case - get user location as flow`() = testCoroutineRule.runTest {
        val location = mockk<Location>()
        every { location.latitude } returns DEFAULT_LATITUDE
        every { location.longitude } returns DEFAULT_LONGITUDE
        
        val locationResult = mockk<LocationResult>()
        every { locationResult.lastLocation?.latitude } returns DEFAULT_LATITUDE
        every { locationResult.lastLocation?.longitude } returns DEFAULT_LONGITUDE
        
        val slot = slot<LocationCallback>()
        every {
            fusedLocationProviderClient.requestLocationUpdates(
                any(),
                any(),
                capture(slot)
            )
        } returns mockk()
        
        repository.getUserLocationAsFlow().test {
            runCurrent()
            slot.captured.onLocationResult(locationResult)
            runCurrent()
            val result = awaitItem()
            
            assertThat(result).isEqualTo(
                provideLocationEntity()
            )
            
            coVerify {
                fusedLocationProviderClient.requestLocationUpdates(
                    any(),
                    any(),
                    capture(slot)
                )
            }
            
            confirmVerified(fusedLocationProviderClient)
        }
    }
    
    private fun provideLocationEntity() = LocationEntity(
        userLatLng = LatLng(
            DEFAULT_LATITUDE,
            DEFAULT_LONGITUDE
        )
    )
}