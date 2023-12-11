package com.despaircorp.data.workers

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.despaircorp.data.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class WorkersDataRepositoryUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val workManager: WorkManager = mockk()

    private lateinit var repository: WorkersDataRepository

    companion object {
        private val DEFAULT_INITIAL_D = 1.seconds
    }

    @Before
    fun setup() {
        repository = WorkersDataRepository(
            workManager = workManager
        )
    }

    @Test
    fun `nominal case - enqueue notification worker`() = testCoroutineRule.runTest {
        coEvery {
            workManager.enqueueUniquePeriodicWork(
                "a_cool_worker",
                ExistingPeriodicWorkPolicy.KEEP,
                any(),
            )
        } returns mockk()

        repository.enqueueNotificationWorker(DEFAULT_INITIAL_D)

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