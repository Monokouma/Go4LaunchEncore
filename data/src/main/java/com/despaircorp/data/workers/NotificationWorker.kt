package com.despaircorp.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firestore.FirestoreDomainRepository
import com.despaircorp.domain.notifications.NotificationDomainRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository,
    private val firestoreDomainRepository: FirestoreDomainRepository,
    private val notificationDomainRepository: NotificationDomainRepository
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val (uid, displayName) = firestoreDomainRepository.getUser(firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid)
        notificationDomainRepository.notify(displayName, uid)
        return Result.success()
    }
}