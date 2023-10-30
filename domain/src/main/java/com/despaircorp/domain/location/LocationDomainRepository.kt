package com.despaircorp.domain.location

import com.despaircorp.domain.location.model.LocationEntity

interface LocationDomainRepository {
    suspend fun getUserLocation(): LocationEntity
}