package com.despaircorp.data.workers

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.despaircorp.data.utils.CoroutineDispatcherProvider
import com.despaircorp.domain.workers.WorkersDomainRepository
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class WorkersDataRepository @Inject constructor(
    private val dispatcherProvider: CoroutineDispatcherProvider,
    private val workManager: WorkManager
) : WorkersDomainRepository {
    override suspend fun enqueueNotificationWorker(initialD: Long): Boolean =
        withContext(dispatcherProvider.io) {
            val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(initialD, TimeUnit.MILLISECONDS)
                .build()
            try {
                workManager.enqueueUniquePeriodicWork(
                    "a_cool_worker",
                    ExistingPeriodicWorkPolicy.KEEP,
                    workRequest
                )
                true
            } catch (e: Exception) {
                ensureActive()
                false
            }
        }
    
    
}