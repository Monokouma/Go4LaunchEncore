package com.despaircorp.domain.room

import com.despaircorp.domain.room.model.NotificationsStateEnum
import javax.inject.Inject

class UpdateNotificationStateUseCase @Inject constructor(
    private val roomDomainRepository: RoomDomainRepository,
) {
    suspend fun invoke(isEnabled: Boolean): Boolean {
        val notificationState = if (isEnabled) {
            NotificationsStateEnum.ENABLED
        } else {
            NotificationsStateEnum.DISABLED
        }
        
        return (roomDomainRepository.updateNotificationPreferences(notificationState) >= 1)
    }
}