package com.despaircorp.domain.notifications

import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CreateNotificationChannelUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val notificationDomainRepository: NotificationDomainRepository = mockk()
    
    private val useCase = CreateNotificationChannelUseCase(
        notificationDomainRepository
    )
    
    @Before
    fun setup() {
        coJustRun { notificationDomainRepository.createChannel() }
    }
    
    @Test
    fun `nominal case - creation success`() = testCoroutineRule.runTest {
        useCase.invoke()
        
        coVerify {
            notificationDomainRepository.createChannel()
        }
        
        confirmVerified(notificationDomainRepository)
    }
    
    @Test
    fun `nominal case - creation failure`() = testCoroutineRule.runTest {
        coJustRun { notificationDomainRepository.createChannel() }
        
        useCase.invoke()
        
        coVerify {
            notificationDomainRepository.createChannel()
        }
        
        confirmVerified(notificationDomainRepository)
    }
}