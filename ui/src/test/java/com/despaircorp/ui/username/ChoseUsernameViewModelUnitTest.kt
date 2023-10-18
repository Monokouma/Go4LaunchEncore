package com.despaircorp.ui.username

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.firebaseAuth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firestore.UpdateUsernameUseCase
import com.despaircorp.domain.room.InitUserPreferencesUseCase
import com.despaircorp.domain.room.IsNotificationsEnabledUseCase
import com.despaircorp.domain.room.IsUserPreferencesTableExistUseCase
import com.despaircorp.domain.room.model.NotificationsStateEnum
import com.despaircorp.domain.room.model.UserPreferencesDomainEntity
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.TestCoroutineRule
import com.despaircorp.ui.utils.observeForTesting
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChoseUsernameViewModelUnitTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase = mockk()
    private val updateUsernameUseCase: UpdateUsernameUseCase = mockk()
    private val isNotificationsEnabledUseCase: IsNotificationsEnabledUseCase = mockk()
    private val initUserPreferencesUseCase: InitUserPreferencesUseCase = mockk()
    private val isUserPreferencesTableExistUseCase: IsUserPreferencesTableExistUseCase = mockk()
    
    private lateinit var viewModel: ChoseUsernameViewModel
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
        private val DEFAULT_NOTIF_STATE_NOT_KNOW = NotificationsStateEnum.NOT_KNOW
        private val DEFAULT_NOTIF_STATE_ENABLED = NotificationsStateEnum.ENABLED
        private val DEFAULT_NOTIF_STATE_DISABLED = NotificationsStateEnum.DISABLED
        
        
    }
    
    @Before
    fun setup() {
        coEvery { getAuthenticatedUserUseCase.invoke().uid } returns DEFAULT_UID
        coEvery { updateUsernameUseCase.invoke(DEFAULT_DISPLAY_NAME, DEFAULT_UID) } returns true
        
        viewModel = ChoseUsernameViewModel(
            getAuthenticatedUserUseCase = getAuthenticatedUserUseCase,
            updateUsernameUseCase = updateUsernameUseCase,
            isNotificationsEnabledUseCase = isNotificationsEnabledUseCase,
            initUserPreferencesUseCase = initUserPreferencesUseCase,
            isUserPreferencesTableExistUseCase = isUserPreferencesTableExistUseCase
        )
    }
    
    @Test
    fun `nominal case - update username with success`() = testCoroutineRule.runTest {
        coEvery { isUserPreferencesTableExistUseCase.invoke() } returns true
        coEvery { isNotificationsEnabledUseCase.invoke() } returns UserPreferencesDomainEntity(
            NotificationsStateEnum.ENABLED
        )
        viewModel.onUsernameTextChanged(DEFAULT_DISPLAY_NAME)
        viewModel.onContinueButtonClicked()
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.peekContent()).isEqualTo(ChoseUsernameAction.Continue)
        }
    }
    
    @Test
    fun `nominal case - update username with empty username`() = testCoroutineRule.runTest {
        viewModel.onUsernameTextChanged("")
        viewModel.onContinueButtonClicked()
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.peekContent()).isEqualTo(ChoseUsernameAction.Error(R.string.error_username_empty))
        }
    }
    
    @Test
    fun `nominal case - update username with error update`() = testCoroutineRule.runTest {
        coEvery { updateUsernameUseCase.invoke(DEFAULT_DISPLAY_NAME, DEFAULT_UID) } returns false
        
        viewModel.onUsernameTextChanged(DEFAULT_DISPLAY_NAME)
        viewModel.onContinueButtonClicked()
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.peekContent()).isEqualTo(ChoseUsernameAction.Error(R.string.error_occurred))
        }
    }
    
    @Test
    fun `nominal case - table exist and notification state is not know`() =
        testCoroutineRule.runTest {
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns true
            coEvery { isNotificationsEnabledUseCase.invoke().isNotificationsEnabled } returns NotificationsStateEnum.NOT_KNOW
            
            viewModel.onUsernameTextChanged(DEFAULT_DISPLAY_NAME)
            viewModel.onContinueButtonClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.peekContent()).isEqualTo(ChoseUsernameAction.EnableNotifications)
            }
        }
    
    @Test
    fun `nominal case - table not exist`() =
        testCoroutineRule.runTest {
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns false
            coEvery {
                initUserPreferencesUseCase.invoke(
                    provideUserPreferencesDomainEntity(
                        DEFAULT_NOTIF_STATE_NOT_KNOW
                    )
                )
            } returns true
            
            viewModel.onUsernameTextChanged(DEFAULT_DISPLAY_NAME)
            viewModel.onContinueButtonClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.peekContent()).isEqualTo(ChoseUsernameAction.EnableNotifications)
            }
        }
    
    @Test
    fun `error case - table not exist`() =
        testCoroutineRule.runTest {
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns false
            coEvery {
                initUserPreferencesUseCase.invoke(
                    provideUserPreferencesDomainEntity(
                        DEFAULT_NOTIF_STATE_NOT_KNOW
                    )
                )
            } returns false
            
            viewModel.onUsernameTextChanged(DEFAULT_DISPLAY_NAME)
            viewModel.onContinueButtonClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.peekContent()).isEqualTo(ChoseUsernameAction.Error(R.string.error_occurred))
            }
        }
    
    private fun provideUserPreferencesDomainEntity(state: NotificationsStateEnum): UserPreferencesDomainEntity {
        return when (state) {
            NotificationsStateEnum.ENABLED -> UserPreferencesDomainEntity(isNotificationsEnabled = DEFAULT_NOTIF_STATE_ENABLED)
            NotificationsStateEnum.DISABLED -> UserPreferencesDomainEntity(isNotificationsEnabled = DEFAULT_NOTIF_STATE_DISABLED)
            NotificationsStateEnum.NOT_KNOW -> UserPreferencesDomainEntity(isNotificationsEnabled = DEFAULT_NOTIF_STATE_NOT_KNOW)
            else -> UserPreferencesDomainEntity(isNotificationsEnabled = DEFAULT_NOTIF_STATE_NOT_KNOW)
        }
    }
}