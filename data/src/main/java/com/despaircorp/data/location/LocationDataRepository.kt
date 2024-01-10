package com.despaircorp.data.location

import android.annotation.SuppressLint
import android.location.Location
import com.despaircorp.data.utils.CoroutineDispatcherProvider
import com.despaircorp.domain.location.LocationDomainRepository
import com.despaircorp.domain.location.model.LocationEntity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


@SuppressLint("MissingPermission")
class LocationDataRepository @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : LocationDomainRepository {
    
    override suspend fun getUserLocation(): LocationEntity =
        withContext(coroutineDispatcherProvider.io) {
            val location = fusedLocationProviderClient.lastLocation.await()
            LocationEntity(
                LatLng(
                    location.latitude,
                    location.longitude
                )
            )
        }
    
    override suspend fun getDistanceBetweenPlaceAndUser(
        userLocation: LocationEntity,
        restaurantLat: Double,
        restaurantLong: Double,
    ): Int = withContext(Dispatchers.Default) {
        val result = FloatArray(1)
        Location.distanceBetween(
            userLocation.userLatLng.latitude,
            userLocation.userLatLng.longitude,
            restaurantLat,
            restaurantLong,
            result
        )
        result.first().toInt()
    }
    
    override fun getUserLocationAsFlow(): Flow<LocationEntity> = callbackFlow {
        val locationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    
                    trySend(
                        LocationEntity(
                            userLatLng = LatLng(it.latitude, it.longitude)
                        )
                    )
                }
            }
        }
        
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 60_000L)
            .setMinUpdateDistanceMeters(4f)
            .build()
        
        
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            coroutineDispatcherProvider.io.asExecutor(),
            locationCallback,
        )
        
        awaitClose { fusedLocationProviderClient.removeLocationUpdates(locationCallback) }
    }
}