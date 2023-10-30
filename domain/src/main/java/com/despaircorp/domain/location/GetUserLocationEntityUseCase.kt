package com.despaircorp.domain.location

import com.despaircorp.domain.location.model.LocationEntity
import javax.inject.Inject

class GetUserLocationEntityUseCase @Inject constructor(
    private val locationDomainRepository: LocationDomainRepository
) {
    suspend fun invoke(): LocationEntity = locationDomainRepository.getUserLocation()
}