package com.despaircorp.domain.workers

import android.util.Log
import java.util.Calendar
import javax.inject.Inject

class EnqueueLaunchNotificationWorker @Inject constructor(
    private val workersDomainRepository: WorkersDomainRepository
) {
    suspend fun invoke(): Boolean =
        workersDomainRepository.enqueueNotificationWorker(getInitialDelay())
    
    private fun getInitialDelay(): Long {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 12)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val nextRun = calendar.timeInMillis
        if (nextRun < now) { // if 12 PM has already passed, schedule for next day
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        Log.i("Monokouma", calendar.toString())
        return calendar.timeInMillis - now
    }
    
}