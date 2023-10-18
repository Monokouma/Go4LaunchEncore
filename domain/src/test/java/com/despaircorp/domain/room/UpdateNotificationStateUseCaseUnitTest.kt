package com.despaircorp.domain.room

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.despaircorp.domain.room.model.NotificationsStateEnum
import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UpdateNotificationStateUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val roomDomainRepository: RoomDomainRepository = mockk()
    
    private val useCase = UpdateNotificationStateUseCase(
        roomDomainRepository = roomDomainRepository
    )
    
    companion object {
        private val DEFAULT_NOTIF_STATE_ENABLED = NotificationsStateEnum.ENABLED
        private val DEFAULT_NOTIF_STATE_DISABLED = NotificationsStateEnum.DISABLED
    }
    
    @Before
    fun setup() {
        coEvery { roomDomainRepository.updateNotificationPreferences(DEFAULT_NOTIF_STATE_ENABLED) } returns 1
    }
    
    @Test
    fun `nominal case - update success with enabled`() = testCoroutineRule.runTest {
        val result = useCase.invoke(true)
        assertThat(result).isTrue()
        
        coVerify {
            roomDomainRepository.updateNotificationPreferences(DEFAULT_NOTIF_STATE_ENABLED)
        }
        confirmVerified(roomDomainRepository)
    }
    
    @Test
    fun `error case - update success with enabled`() = testCoroutineRule.runTest {
        coEvery { roomDomainRepository.updateNotificationPreferences(DEFAULT_NOTIF_STATE_ENABLED) } returns 0
        
        val result = useCase.invoke(true)
        assertThat(result).isFalse()
        
        coVerify {
            roomDomainRepository.updateNotificationPreferences(DEFAULT_NOTIF_STATE_ENABLED)
        }
        confirmVerified(roomDomainRepository)
    }
    
    @Test
    fun `nominal case - update success with disabled`() = testCoroutineRule.runTest {
        coEvery { roomDomainRepository.updateNotificationPreferences(DEFAULT_NOTIF_STATE_DISABLED) } returns 1
        
        val result = useCase.invoke(false)
        assertThat(result).isTrue()
        
        coVerify {
            roomDomainRepository.updateNotificationPreferences(DEFAULT_NOTIF_STATE_DISABLED)
        }
        confirmVerified(roomDomainRepository)
    }
    
    @Test
    fun `error case - update success with disabled`() = testCoroutineRule.runTest {
        coEvery { roomDomainRepository.updateNotificationPreferences(DEFAULT_NOTIF_STATE_DISABLED) } returns 0
        
        val result = useCase.invoke(false)
        assertThat(result).isFalse()
        
        coVerify {
            roomDomainRepository.updateNotificationPreferences(DEFAULT_NOTIF_STATE_DISABLED)
        }
        confirmVerified(roomDomainRepository)
    }
}