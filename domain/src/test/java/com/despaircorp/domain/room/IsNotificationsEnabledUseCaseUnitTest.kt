package com.despaircorp.domain.room

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.room.model.NotificationsStateEnum
import com.despaircorp.domain.room.model.UserPreferencesDomainEntity
import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class IsNotificationsEnabledUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val roomDomainRepository: RoomDomainRepository = mockk()
    
    private val useCase = IsNotificationsEnabledUseCase(
        roomDomainRepository = roomDomainRepository
    )
    
    companion object {
        private val DEFAULT_NOTIF_STATE_DISABLED = NotificationsStateEnum.DISABLED
    }
    
    @Before
    fun setup() {
        coEvery { roomDomainRepository.isNotificationEnabled() } returns provideUserPreferencesDomainEntity(
            DEFAULT_NOTIF_STATE_DISABLED
        )
    }
    
    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        val result = useCase.invoke()
        
        assertThat(result).isEqualTo(
            provideUserPreferencesDomainEntity(DEFAULT_NOTIF_STATE_DISABLED)
        )
        
        coVerify {
            roomDomainRepository.isNotificationEnabled()
        }
        
        confirmVerified(roomDomainRepository)
    }
    
    private fun provideUserPreferencesDomainEntity(state: NotificationsStateEnum): UserPreferencesDomainEntity {
        return when (state) {
            NotificationsStateEnum.ENABLED -> UserPreferencesDomainEntity(isNotificationsEnabled = NotificationsStateEnum.ENABLED)
            NotificationsStateEnum.DISABLED -> UserPreferencesDomainEntity(isNotificationsEnabled = NotificationsStateEnum.DISABLED)
            NotificationsStateEnum.NOT_KNOW -> UserPreferencesDomainEntity(isNotificationsEnabled = NotificationsStateEnum.NOT_KNOW)
            else -> UserPreferencesDomainEntity(isNotificationsEnabled = NotificationsStateEnum.NOT_KNOW)
        }
    }
}