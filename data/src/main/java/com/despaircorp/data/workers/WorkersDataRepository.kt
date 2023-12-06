package com.despaircorp.data.workers

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.despaircorp.domain.workers.WorkersDomainRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Duration


class WorkersDataRepository @Inject constructor(
    private val workManager: WorkManager
) : WorkersDomainRepository {
    override fun enqueueNotificationWorker(initialDuration: Duration) {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDuration.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "a_cool_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

}