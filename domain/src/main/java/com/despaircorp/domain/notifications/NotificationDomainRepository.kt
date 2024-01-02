package com.despaircorp.domain.notifications

interface NotificationDomainRepository {
    fun createChannel()
    fun notify(username: String, sentence: String)
}