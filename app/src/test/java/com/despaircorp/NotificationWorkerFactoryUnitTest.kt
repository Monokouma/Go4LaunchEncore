package com.despaircorp

import android.app.Application
import androidx.work.WorkerParameters
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.data.workers.NotificationWorker
import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firestore.FirestoreDomainRepository
import com.despaircorp.domain.notifications.NotificationDomainRepository
import com.despaircorp.go4launchencore.factory.NotificationWorkerFactory
import com.despaircorp.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NotificationWorkerFactoryUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository = mockk()
    private val firestoreDomainRepository: FirestoreDomainRepository = mockk()
    private val notificationDomainRepository: NotificationDomainRepository = mockk()
    private val application: Application = mockk()
    private val workerParameters: WorkerParameters = mockk()
    
    private val notificationWorkerFactory = NotificationWorkerFactory(
        firebaseAuthDomainRepository = firebaseAuthDomainRepository,
        firestoreDomainRepository = firestoreDomainRepository,
        notificationDomainRepository = notificationDomainRepository
    )
    
    @Before
    fun setup() {
        coEvery { application.applicationContext } returns mockk()
        coEvery { workerParameters.taskExecutor.serialTaskExecutor } returns mockk()
        coEvery { workerParameters.id } returns mockk()
    }
    
    @Test
    fun `nominal case - create worker`() = testCoroutineRule.runTest {
        val result = notificationWorkerFactory.createWorker(
            application.applicationContext,
            NotificationWorker::class.java.name,
            workerParameters
        )
        
        assertThat(result.id).isEqualTo(
            NotificationWorker(
                context = application.applicationContext,
                workerParams = workerParameters,
                firebaseAuthDomainRepository = firebaseAuthDomainRepository,
                firestoreDomainRepository = firestoreDomainRepository,
                notificationDomainRepository = notificationDomainRepository
            ).id
        )
    }
}