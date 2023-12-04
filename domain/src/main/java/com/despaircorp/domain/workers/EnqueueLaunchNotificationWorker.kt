package com.despaircorp.domain.workers

import java.time.Clock
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class EnqueueLaunchNotificationWorker @Inject constructor(
    private val workersDomainRepository: WorkersDomainRepository,
    private val clock: Clock,
) {
    suspend fun invoke(): Boolean =
        workersDomainRepository.enqueueNotificationWorker(getInitialDelay())
    
    private fun getInitialDelay(): Long {
        val now = LocalDateTime.now(clock)
        var nextRun = now.withHour(12).withMinute(0).withSecond(0)
        
        // If 12 PM has already passed, schedule for the next day
        if (nextRun.isBefore(now)) {
            nextRun = nextRun.plusDays(1)
        }
        
        // Calculate delay in milliseconds
        return ChronoUnit.MILLIS.between(now, nextRun)
    }
    
}