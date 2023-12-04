package com.despaircorp.domain.workers

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coEvery
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
        coEvery { workersDomainRepository.enqueueNotificationWorker(DEFAULT_INITIAL_D) } returns true
    }
    
    @Test
    fun `nominal case - success`() = testCoroutineRule.runTest {
        val result = useCase.invoke()
        
        assertThat(result).isTrue()
        
        coVerify { workersDomainRepository.enqueueNotificationWorker(DEFAULT_INITIAL_D) }
        
        confirmVerified(workersDomainRepository)
    }
    
    @Test
    fun `error case - failure`() = testCoroutineRule.runTest {
        coEvery { workersDomainRepository.enqueueNotificationWorker(DEFAULT_INITIAL_D) } returns false
        
        val result = useCase.invoke()
        
        assertThat(result).isFalse()
        
        coVerify { workersDomainRepository.enqueueNotificationWorker(DEFAULT_INITIAL_D) }
        
        confirmVerified(workersDomainRepository)
    }
}