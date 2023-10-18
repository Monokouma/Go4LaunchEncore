package com.despaircorp.domain.room

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
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

class InitUserPreferencesUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val roomDomainRepository: RoomDomainRepository = mockk()
    
    private val useCase = InitUserPreferencesUseCase(
        roomDomainRepository = roomDomainRepository
    )
    
    companion object {
        private val DEFAULT_NOTIF_STATE_DISABLED = NotificationsStateEnum.DISABLED
    }
    
    @Before
    fun setup() {
        coEvery {
            roomDomainRepository.insertUserPreferences(
                provideUserPreferencesDomainEntity(
                    DEFAULT_NOTIF_STATE_DISABLED
                )
            )
        } returns 1L
    }
    
    @Test
    fun `nominal case - success`() = testCoroutineRule.runTest {
        val result =
            useCase.invoke(provideUserPreferencesDomainEntity(DEFAULT_NOTIF_STATE_DISABLED))
        
        assertThat(result).isTrue()
        
        coVerify {
            roomDomainRepository.insertUserPreferences(
                provideUserPreferencesDomainEntity(
                    DEFAULT_NOTIF_STATE_DISABLED
                )
            )
        }
        
        confirmVerified(roomDomainRepository)
    }
    
    @Test
    fun `error case - fail`() = testCoroutineRule.runTest {
        coEvery {
            roomDomainRepository.insertUserPreferences(
                provideUserPreferencesDomainEntity(
                    DEFAULT_NOTIF_STATE_DISABLED
                )
            )
        } returns 0L
        val result =
            useCase.invoke(provideUserPreferencesDomainEntity(DEFAULT_NOTIF_STATE_DISABLED))
        
        assertThat(result).isFalse()
        
        coVerify {
            roomDomainRepository.insertUserPreferences(
                provideUserPreferencesDomainEntity(
                    DEFAULT_NOTIF_STATE_DISABLED
                )
            )
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