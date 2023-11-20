package com.despaircorp.domain.notifications

interface NotificationDomainRepository {
    suspend fun createChannel(): Boolean
    suspend fun notify(username: String, uid: String): Boolean
}