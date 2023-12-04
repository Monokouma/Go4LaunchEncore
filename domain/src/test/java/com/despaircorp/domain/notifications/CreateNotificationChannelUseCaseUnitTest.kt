package com.despaircorp.domain.notifications

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

class CreateNotificationChannelUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val notificationDomainRepository: NotificationDomainRepository = mockk()
    
    private val useCase = CreateNotificationChannelUseCase(
        notificationDomainRepository
    )
    
    @Before
    fun setup() {
        coEvery { notificationDomainRepository.createChannel() } returns true
    }
    
    @Test
    fun `nominal case - creation success`() = testCoroutineRule.runTest {
        val result = useCase.invoke()
        
        assertThat(result).isTrue()
        
        coVerify {
            notificationDomainRepository.createChannel()
        }
        
        confirmVerified(notificationDomainRepository)
    }
    
    @Test
    fun `nominal case - creation failure`() = testCoroutineRule.runTest {
        coEvery { notificationDomainRepository.createChannel() } returns false
        
        val result = useCase.invoke()
        
        assertThat(result).isFalse()
        
        coVerify {
            notificationDomainRepository.createChannel()
        }
        
        confirmVerified(notificationDomainRepository)
    }
}