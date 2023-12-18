package com.despaircorp.domain.room

import javax.inject.Inject

class IsUserPreferencesTableExistUseCase @Inject constructor(
    private val roomDomainRepository: RoomDomainRepository,
) {
    suspend fun invoke() = roomDomainRepository.exist()
}