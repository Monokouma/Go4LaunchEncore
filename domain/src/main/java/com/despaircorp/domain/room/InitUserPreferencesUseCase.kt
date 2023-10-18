package com.despaircorp.domain.room

import com.despaircorp.domain.room.model.UserPreferencesDomainEntity
import javax.inject.Inject

class InitUserPreferencesUseCase @Inject constructor(
    private val roomDomainRepository: RoomDomainRepository,
    
    ) {
    suspend fun invoke(userPreferencesDomainEntity: UserPreferencesDomainEntity): Boolean =
        roomDomainRepository.insertUserPreferences(userPreferencesDomainEntity) >= 1L
}