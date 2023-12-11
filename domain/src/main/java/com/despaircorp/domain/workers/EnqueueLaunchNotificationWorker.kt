package com.despaircorp.domain.workers

import java.time.Clock
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class EnqueueLaunchNotificationWorker @Inject constructor(
    private val workersDomainRepository: WorkersDomainRepository,
    private val clock: Clock,
) {
    fun invoke() {
        workersDomainRepository.enqueueNotificationWorker(getInitialDelay())
    }

    private fun getInitialDelay(): Duration {
        val now = LocalDateTime.now(clock)
        val nextRun = now.withHour(12).withMinute(0).withSecond(0).let {
            // If 12 PM has already passed, schedule for the next day
            if (it.isBefore(now)) {
                it.plusDays(1)
            } else {
                it
            }
        }

        // Calculate delay in milliseconds
        return ChronoUnit.MILLIS.between(now, nextRun).milliseconds
    }

}