package com.despaircorp.data.room

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.despaircorp.data.room.dao.UserPreferencesDao
import com.despaircorp.data.room.entities.UserPreferencesDataEntity
import com.despaircorp.data.utils.TestCoroutineRule
import com.despaircorp.domain.room.model.NotificationsStateEnum
import com.despaircorp.domain.room.model.UserPreferencesDomainEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RoomDataRepositoryUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val userPreferencesDao: UserPreferencesDao = mockk()
    
    private val repository = RoomDataRepository(
        coroutineDispatcherProvider = testCoroutineRule.getTestCoroutineDispatcherProvider(),
        userPreferencesDao = userPreferencesDao
    )
    
    companion object {
        private val DEFAULT_NOTIF_STATE_NOT_KNOW = NotificationsStateEnum.NOT_KNOW
        private val DEFAULT_NOTIF_STATE_ENABLED = NotificationsStateEnum.ENABLED
        
    }
    
    @Before
    fun setup() {
        coEvery { userPreferencesDao.getUserPreferences() } returns provideUserPreferencesDataEntity(
            DEFAULT_NOTIF_STATE_NOT_KNOW
        )
        coEvery { userPreferencesDao.exist() } returns true
        coEvery {
            userPreferencesDao.insertUserPreferencesEntity(
                provideUserPreferencesDataEntity(
                    DEFAULT_NOTIF_STATE_NOT_KNOW
                )
            )
        } returns 1L
        coEvery { userPreferencesDao.updateUserPreferencesNotification(DEFAULT_NOTIF_STATE_ENABLED) } returns 1
    }
    
    @Test
    fun `nominal case - get notification entity`() = testCoroutineRule.runTest {
        val result = repository.isNotificationEnabled()
        
        assertThat(result).isEqualTo(
            provideUserPreferencesDomainEntity(DEFAULT_NOTIF_STATE_NOT_KNOW)
        )
        
        coVerify {
            userPreferencesDao.getUserPreferences()
        }
        
        confirmVerified(userPreferencesDao)
    }
    
    @Test
    fun `nominal case - insert user preferences`() = testCoroutineRule.runTest {
        val result = repository.insertUserPreferences(
            provideUserPreferencesDomainEntity(
                DEFAULT_NOTIF_STATE_NOT_KNOW
            )
        )
        
        assertThat(result).isEqualTo(1L)
        
        coVerify {
            userPreferencesDao.insertUserPreferencesEntity(
                provideUserPreferencesDataEntity(
                    DEFAULT_NOTIF_STATE_NOT_KNOW
                )
            )
        }
        confirmVerified(userPreferencesDao)
    }
    
    @Test
    fun `error case - insert user preferences`() = testCoroutineRule.runTest {
        coEvery {
            userPreferencesDao.insertUserPreferencesEntity(
                provideUserPreferencesDataEntity(
                    DEFAULT_NOTIF_STATE_NOT_KNOW
                )
            )
        } returns 0L
        
        val result = repository.insertUserPreferences(
            provideUserPreferencesDomainEntity(
                DEFAULT_NOTIF_STATE_NOT_KNOW
            )
        )
        
        assertThat(result).isEqualTo(0L)
        
        coVerify {
            userPreferencesDao.insertUserPreferencesEntity(
                provideUserPreferencesDataEntity(
                    DEFAULT_NOTIF_STATE_NOT_KNOW
                )
            )
        }
        confirmVerified(userPreferencesDao)
    }
    
    @Test
    fun `nominal case - user preferences table exist`() = testCoroutineRule.runTest {
        val result = repository.exist()
        
        assertThat(result).isTrue()
        
        coVerify {
            userPreferencesDao.exist()
        }
        
        confirmVerified(userPreferencesDao)
    }
    
    @Test
    fun `edge case - user preferences table not exist`() = testCoroutineRule.runTest {
        coEvery { userPreferencesDao.exist() } returns false
        val result = repository.exist()
        
        assertThat(result).isFalse()
        
        coVerify {
            userPreferencesDao.exist()
        }
        
        confirmVerified(userPreferencesDao)
    }
    
    @Test
    fun `nominal case - update user notifications state`() = testCoroutineRule.runTest {
        val result = repository.updateNotificationPreferences(DEFAULT_NOTIF_STATE_ENABLED)
        
        assertThat(result).isEqualTo(1)
        
        coVerify {
            userPreferencesDao.updateUserPreferencesNotification(DEFAULT_NOTIF_STATE_ENABLED)
        }
        confirmVerified(userPreferencesDao)
    }
    
    @Test
    fun `error case - update user notifications state`() = testCoroutineRule.runTest {
        coEvery {
            userPreferencesDao.updateUserPreferencesNotification(DEFAULT_NOTIF_STATE_ENABLED)
        } returns 0
        val result = repository.updateNotificationPreferences(DEFAULT_NOTIF_STATE_ENABLED)
        
        assertThat(result).isEqualTo(0)
        
        coVerify {
            userPreferencesDao.updateUserPreferencesNotification(DEFAULT_NOTIF_STATE_ENABLED)
        }
        confirmVerified(userPreferencesDao)
    }
    
    //Region Out
    private fun provideUserPreferencesDataEntity(state: NotificationsStateEnum): UserPreferencesDataEntity {
        return when (state) {
            NotificationsStateEnum.ENABLED -> UserPreferencesDataEntity(isNotificationEnable = NotificationsStateEnum.ENABLED)
            NotificationsStateEnum.DISABLED -> UserPreferencesDataEntity(isNotificationEnable = NotificationsStateEnum.DISABLED)
            NotificationsStateEnum.NOT_KNOW -> UserPreferencesDataEntity(isNotificationEnable = NotificationsStateEnum.NOT_KNOW)
            else -> UserPreferencesDataEntity(isNotificationEnable = NotificationsStateEnum.NOT_KNOW)
        }
    }
    
    private fun provideUserPreferencesDomainEntity(state: NotificationsStateEnum): UserPreferencesDomainEntity {
        return when (state) {
            NotificationsStateEnum.ENABLED -> UserPreferencesDomainEntity(isNotificationsEnabled = NotificationsStateEnum.ENABLED)
            NotificationsStateEnum.DISABLED -> UserPreferencesDomainEntity(isNotificationsEnabled = NotificationsStateEnum.DISABLED)
            NotificationsStateEnum.NOT_KNOW -> UserPreferencesDomainEntity(isNotificationsEnabled = NotificationsStateEnum.NOT_KNOW)
            else -> UserPreferencesDomainEntity(isNotificationsEnabled = NotificationsStateEnum.NOT_KNOW)
        }
    }
    //End Region Out
}