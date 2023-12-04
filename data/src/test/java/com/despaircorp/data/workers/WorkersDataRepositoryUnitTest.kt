package com.despaircorp.data.workers

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.despaircorp.data.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.isActive
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WorkersDataRepositoryUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val workManager: WorkManager = mockk()
    
    private lateinit var repository: WorkersDataRepository
    
    companion object {
        private const val DEFAULT_INITIAL_D = 1000L
    }
    
    @Before
    fun setup() {
        repository = WorkersDataRepository(
            dispatcherProvider = testCoroutineRule.getTestCoroutineDispatcherProvider(),
            workManager = workManager
        )
    }
    
    @Test
    fun `nominal case - enqueue notification worker`() = testCoroutineRule.runTest {
        val slot = slot<PeriodicWorkRequest>()
        
        coEvery {
            workManager.enqueueUniquePeriodicWork(
                "a_cool_worker",
                ExistingPeriodicWorkPolicy.KEEP,
                capture(slot)
            )
        } returns mockk()
        
        val result = repository.enqueueNotificationWorker(DEFAULT_INITIAL_D)
        
        assertThat(result).isTrue()
        
        coVerify {
            workManager.enqueueUniquePeriodicWork(
                "a_cool_worker",
                ExistingPeriodicWorkPolicy.KEEP,
                any()
            )
        }
        
        confirmVerified(workManager)
    }
    
    @Test
    fun `error case - enqueue notification worker`() = testCoroutineRule.runTest {
        val slot = slot<PeriodicWorkRequest>()
        
        coEvery {
            workManager.enqueueUniquePeriodicWork(
                "a_cool_worker",
                ExistingPeriodicWorkPolicy.KEEP,
                capture(slot)
            )
        } throws Exception()
        
        val result = repository.enqueueNotificationWorker(DEFAULT_INITIAL_D)
        
        assertThat(result).isFalse()
        assertThat(this.isActive).isTrue()
        
        coVerify {
            workManager.enqueueUniquePeriodicWork(
                "a_cool_worker",
                ExistingPeriodicWorkPolicy.KEEP,
                any()
            )
        }
        
        confirmVerified(workManager)
    }
}