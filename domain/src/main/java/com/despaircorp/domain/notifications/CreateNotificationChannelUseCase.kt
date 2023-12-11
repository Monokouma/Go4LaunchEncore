package com.despaircorp.domain.notifications

import javax.inject.Inject

class CreateNotificationChannelUseCase @Inject constructor(
    private val notificationDomainRepository: NotificationDomainRepository
) {
    fun invoke() {
        notificationDomainRepository.createChannel()
    }
}