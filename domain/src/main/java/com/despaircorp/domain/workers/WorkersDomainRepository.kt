package com.despaircorp.domain.workers

import kotlin.time.Duration

interface WorkersDomainRepository {
    fun enqueueNotificationWorker(initialDuration: Duration)
}