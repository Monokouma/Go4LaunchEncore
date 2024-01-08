package com.despaircorp.domain.workers

import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class EnqueueLaunchNotificationWorkerUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val workersDomainRepository: WorkersDomainRepository = mockk()
    
    private val useCase = EnqueueLaunchNotificationWorker(
        workersDomainRepository = workersDomainRepository,
        clock = Clock.fixed(
            Instant.ofEpochMilli(
                1701710764 //Mon Dec 04 2023 17:26:04 GMT+0000
            ), ZoneOffset.UTC
        )
    )
    
    companion object {
        private const val DEFAULT_INITIAL_D = 69490000L
    }
    
    @Before
    fun setup() {
        coJustRun { workersDomainRepository.enqueueNotificationWorker(any()) }
    }
    
    @Test
    fun `nominal case - success`() = testCoroutineRule.runTest {
        val result = useCase.invoke()
        
        coVerify { workersDomainRepository.enqueueNotificationWorker(any()) }
        
        confirmVerified(workersDomainRepository)
    }
    
    @Test
    fun `error case - failure`() = testCoroutineRule.runTest {
        coJustRun { workersDomainRepository.enqueueNotificationWorker(any()) }
        
        useCase.invoke()
        
        
        coVerify { workersDomainRepository.enqueueNotificationWorker(any()) }
        
        confirmVerified(workersDomainRepository)
    }
}