package com.despaircorp.ui.notifications_preferences

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.room.UpdateNotificationStateUseCase
import com.despaircorp.ui.R
import com.despaircorp.ui.notification_preferences.NotificationsPreferencesAction
import com.despaircorp.ui.notification_preferences.NotificationsPreferencesViewModel
import com.despaircorp.ui.utils.TestCoroutineRule
import com.despaircorp.ui.utils.observeForTesting
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NotificationsPreferencesViewModelUnitTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val updateNotificationStateUseCase: UpdateNotificationStateUseCase = mockk()
    
    private lateinit var viewModel: NotificationsPreferencesViewModel
    
    @Before
    fun setup() {
        coEvery { updateNotificationStateUseCase.invoke(true) } returns true
        
        viewModel = NotificationsPreferencesViewModel(
            updateNotificationStateUseCase = updateNotificationStateUseCase
        )
    }
    
    @Test
    fun `nominal case - notif enabled`() = testCoroutineRule.runTest {
        viewModel.onNotificationStateChanged(true)
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(NotificationsPreferencesAction.Success)
        }
    }
    
    @Test
    fun `nominal case - notif disabled`() = testCoroutineRule.runTest {
        
        coEvery { updateNotificationStateUseCase.invoke(false) } returns true
        
        viewModel.onNotificationStateChanged(false)
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(NotificationsPreferencesAction.Success)
        }
    }
    
    @Test
    fun `error case - notif enabled`() = testCoroutineRule.runTest {
        coEvery { updateNotificationStateUseCase.invoke(true) } returns false
        
        viewModel.onNotificationStateChanged(true)
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(NotificationsPreferencesAction.Error(R.string.error_occurred))
        }
    }
    
    @Test
    fun `error case - notif disabled`() = testCoroutineRule.runTest {
        coEvery { updateNotificationStateUseCase.invoke(false) } returns false
        
        viewModel.onNotificationStateChanged(false)
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(NotificationsPreferencesAction.Error(R.string.error_occurred))
        }
    }
}