package com.despaircorp.domain.workers

interface WorkersDomainRepository {
    suspend fun enqueueNotificationWorker(initialD: Long): Boolean
}