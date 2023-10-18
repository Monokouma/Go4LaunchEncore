package com.despaircorp.domain.room

import com.despaircorp.domain.room.model.UserPreferencesDomainEntity
import javax.inject.Inject

class IsNotificationsEnabledUseCase @Inject constructor(
    private val roomDomainRepository: RoomDomainRepository,
    
    ) {
    suspend fun invoke(): UserPreferencesDomainEntity = roomDomainRepository.isNotificationEnabled()
}