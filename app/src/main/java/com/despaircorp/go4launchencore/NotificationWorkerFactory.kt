package com.despaircorp.go4launchencore

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.despaircorp.data.workers.NotificationWorker
import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firestore.FirestoreDomainRepository
import com.despaircorp.domain.notifications.NotificationDomainRepository
import javax.inject.Inject

class NotificationWorkerFactory @Inject constructor(
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository,
    private val firestoreDomainRepository: FirestoreDomainRepository,
    private val notificationDomainRepository: NotificationDomainRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = NotificationWorker(
        appContext,
        workerParameters,
        firebaseAuthDomainRepository,
        firestoreDomainRepository,
        notificationDomainRepository
    )
    
}