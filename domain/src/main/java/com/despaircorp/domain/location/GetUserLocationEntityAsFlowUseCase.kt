package com.despaircorp.domain.location

import com.despaircorp.domain.location.model.LocationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserLocationEntityAsFlowUseCase @Inject constructor(
    private val locationDomainRepository: LocationDomainRepository

) {
    fun invoke(): Flow<LocationEntity> = locationDomainRepository.getUserLocationAsFlow()
}