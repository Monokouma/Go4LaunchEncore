package com.despaircorp.data.workers

import android.app.Application
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.data.utils.TestCoroutineRule
import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firestore.FirestoreDomainRepository
import com.despaircorp.domain.notifications.NotificationDomainRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NotificationWorkerUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val application: Application = mockk()
    
    private val workerParameters: WorkerParameters = mockk()
    
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository = mockk()
    private val firestoreDomainRepository: FirestoreDomainRepository = mockk()
    private val notificationDomainRepository: NotificationDomainRepository = mockk()
    
    private lateinit var notificationWorker: NotificationWorker
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
    }
    
    @Before
    fun setup() {
        every { application.applicationContext } returns mockk()
        every { workerParameters.taskExecutor.serialTaskExecutor } returns mockk()
        coEvery { firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid } returns DEFAULT_UID
        coEvery { firestoreDomainRepository.getUser(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
        coEvery { firestoreDomainRepository.getUser(DEFAULT_UID).uid } returns DEFAULT_UID
        justRun {
            notificationDomainRepository.notify(
                DEFAULT_DISPLAY_NAME,
                DEFAULT_UID
            )
        }
        
        notificationWorker = NotificationWorker(
            context = application.applicationContext,
            workerParams = workerParameters,
            firebaseAuthDomainRepository = firebaseAuthDomainRepository,
            firestoreDomainRepository = firestoreDomainRepository,
            notificationDomainRepository = notificationDomainRepository
        )
    }
    
    @Test
    fun `nominal case - success`() = testCoroutineRule.runTest {
        val result = notificationWorker.doWork()
        
        assertThat(result).isEqualTo(ListenableWorker.Result.success())
        
        coVerify {
            firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid
            firestoreDomainRepository.getUser(DEFAULT_UID).displayName
            firestoreDomainRepository.getUser(DEFAULT_UID).uid
            notificationDomainRepository.notify(
                DEFAULT_DISPLAY_NAME,
                DEFAULT_UID
            )
        }
        
        confirmVerified(
            firestoreDomainRepository,
            firebaseAuthDomainRepository,
            notificationDomainRepository
        )
    }
}