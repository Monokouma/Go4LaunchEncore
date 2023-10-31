package com.despaircorp.ui.main.settings

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.firebase_auth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firebase_auth.UpdateAuthMailAddressUseCase
import com.despaircorp.domain.firebase_auth.UpdateAuthPasswordUseCase
import com.despaircorp.domain.firebase_auth.model.AuthenticateUserEntity
import com.despaircorp.domain.firebase_storage.UpdateUserImageThenGetLinkUseCase
import com.despaircorp.domain.firestore.GetFirestoreUserAsFlowUseCase
import com.despaircorp.domain.firestore.UpdateFirestoreMailAddressUseCase
import com.despaircorp.domain.firestore.UpdateUserImageUseCase
import com.despaircorp.domain.firestore.UpdateUsernameUseCase
import com.despaircorp.domain.firestore.model.FirestoreUserEntity
import com.despaircorp.domain.room.IsNotificationsEnabledUseCase
import com.despaircorp.domain.room.UpdateNotificationStateUseCase
import com.despaircorp.domain.room.model.NotificationsStateEnum
import com.despaircorp.domain.room.model.UserPreferencesDomainEntity
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.TestCoroutineRule
import com.despaircorp.ui.utils.observeForTesting
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class UserSettingsViewModelUnitTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val isNotificationsEnabledUseCase: IsNotificationsEnabledUseCase = mockk()
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase = mockk()
    private val getFirestoreUserAsFlowUseCase: GetFirestoreUserAsFlowUseCase = mockk()
    private val updateNotificationStateUseCase: UpdateNotificationStateUseCase = mockk()
    private val updateAuthMailAddressUseCase: UpdateAuthMailAddressUseCase = mockk()
    private val updateFirestoreMailAddressUseCase: UpdateFirestoreMailAddressUseCase = mockk()
    private val updateUsernameUseCase: UpdateUsernameUseCase = mockk()
    private val updateAuthPasswordUseCase: UpdateAuthPasswordUseCase = mockk()
    private val updateUserImageThenGetLinkUseCase: UpdateUserImageThenGetLinkUseCase = mockk()
    private val updateUserImageUseCase: UpdateUserImageUseCase = mockk()
    
    private lateinit var viewModel: UserSettingsViewModel
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_PASSWORD = "DEFAULT_PASSWORD"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
        private const val DEFAULT_EMAIL = "DEFAULT_EMAIL"
        private const val DEFAULT_PICTURE = "DEFAULT_PICTURE"
        private val DEFAULT_PICTURE_URI = mockk<Uri>()
        private const val DEFAULT_CURRENTLY_EATING = false
        private val DEFAULT_EATING_PLACE_IDE = null
        
        private val DEFAULT_NOTIF_STATE_NOT_KNOW = NotificationsStateEnum.NOT_KNOW
        private val DEFAULT_NOTIF_STATE_ENABLED = NotificationsStateEnum.ENABLED
        private val DEFAULT_NOTIF_STATE_DISABLED = NotificationsStateEnum.DISABLED
    }
    
    @Before
    fun setup() {
        coEvery { getAuthenticatedUserUseCase.invoke().uid } returns DEFAULT_UID
        
        coEvery { getFirestoreUserAsFlowUseCase.invoke(DEFAULT_UID) } returns flowOf(
            FirestoreUserEntity(
                DEFAULT_PICTURE,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_EMAIL,
                DEFAULT_UID,
                DEFAULT_CURRENTLY_EATING,
                DEFAULT_EATING_PLACE_IDE
            )
        )
        
        coEvery { isNotificationsEnabledUseCase.invoke().isNotificationsEnabled } returns DEFAULT_NOTIF_STATE_ENABLED
        
        viewModel = UserSettingsViewModel(
            isNotificationsEnabledUseCase = isNotificationsEnabledUseCase,
            getAuthenticatedUserUseCase = getAuthenticatedUserUseCase,
            getFirestoreUserAsFlowUseCase = getFirestoreUserAsFlowUseCase,
            updateNotificationStateUseCase = updateNotificationStateUseCase,
            updateAuthMailAddressUseCase = updateAuthMailAddressUseCase,
            updateFirestoreMailAddressUseCase = updateFirestoreMailAddressUseCase,
            updateUsernameUseCase = updateUsernameUseCase,
            updateAuthPasswordUseCase = updateAuthPasswordUseCase,
            updateUserImageThenGetLinkUseCase = updateUserImageThenGetLinkUseCase,
            updateUserImageUseCase = updateUserImageUseCase
        )
    }
    
    @Test
    fun `nominal case - get correct infos`() = testCoroutineRule.runTest {
        
        viewModel.viewState.observeForTesting(this) {
            assertThat(it.value).isEqualTo(
                UserSettingsViewState(
                    emailAddress = DEFAULT_EMAIL,
                    displayName = DEFAULT_DISPLAY_NAME,
                    isNotificationsChecked = true,
                    userImageUrl = DEFAULT_PICTURE
                )
            )
        }
    }
    
    @Test
    fun `nominal case - get correct infos with disabled notif`() = testCoroutineRule.runTest {
        coEvery { isNotificationsEnabledUseCase.invoke().isNotificationsEnabled } returns DEFAULT_NOTIF_STATE_DISABLED
        
        viewModel.viewState.observeForTesting(this) {
            assertThat(it.value).isEqualTo(
                UserSettingsViewState(
                    emailAddress = DEFAULT_EMAIL,
                    displayName = DEFAULT_DISPLAY_NAME,
                    isNotificationsChecked = false,
                    userImageUrl = DEFAULT_PICTURE
                )
            )
        }
    }
    
    @Test
    fun `nominal case - update username success`() = testCoroutineRule.runTest {
        coEvery { updateUsernameUseCase.invoke(DEFAULT_DISPLAY_NAME, DEFAULT_UID) } returns true
        viewModel.onUsernameTextChanged(DEFAULT_DISPLAY_NAME)
        viewModel.onValidateUsernameButtonClicked()
        
        viewModel.viewState.observeForTesting(this) {
            assertThat(it.value).isEqualTo(
                UserSettingsViewState(
                    emailAddress = DEFAULT_EMAIL,
                    displayName = DEFAULT_DISPLAY_NAME,
                    isNotificationsChecked = true,
                    userImageUrl = DEFAULT_PICTURE
                )
            )
        }
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(UserSettingsAction.ModificationsSuccess)
        }
    }
    
    @Test
    fun `nominal case - update username fail cause empty username`() = testCoroutineRule.runTest {
        coEvery { updateUsernameUseCase.invoke(DEFAULT_DISPLAY_NAME, DEFAULT_UID) } returns true
        viewModel.onUsernameTextChanged("")
        viewModel.onValidateUsernameButtonClicked()
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(UserSettingsAction.Error(R.string.error_username_empty))
        }
    }
    
    @Test
    fun `nominal case - update username fail cause null username`() = testCoroutineRule.runTest {
        coEvery { updateUsernameUseCase.invoke(DEFAULT_DISPLAY_NAME, DEFAULT_UID) } returns true
        viewModel.onValidateUsernameButtonClicked()
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(UserSettingsAction.Error(R.string.error_username_empty))
        }
    }
    
    @Test
    fun `nominal case - update username fail cause update error`() = testCoroutineRule.runTest {
        coEvery { updateUsernameUseCase.invoke(DEFAULT_DISPLAY_NAME, DEFAULT_UID) } returns false
        viewModel.onUsernameTextChanged(DEFAULT_DISPLAY_NAME)
        viewModel.onValidateUsernameButtonClicked()
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(UserSettingsAction.Error(R.string.error_occurred))
        }
    }
    
    
    @Test
    fun `nominal case - update email null mail`() = testCoroutineRule.runTest {
        viewModel.onValidateMailButtonClicked()
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(UserSettingsAction.Error(R.string.error_mail_empty))
        }
    }
    
    @Test
    fun `nominal case - update email empty mail`() = testCoroutineRule.runTest {
        viewModel.onMailAddressTextChanged("")
        viewModel.onValidateMailButtonClicked()
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(UserSettingsAction.Error(R.string.error_mail_empty))
        }
    }
    
    @Test
    fun `nominal case - update email success`() = testCoroutineRule.runTest {
        coEvery { updateAuthMailAddressUseCase.invoke(DEFAULT_EMAIL) } returns true
        coEvery {
            updateFirestoreMailAddressUseCase.invoke(
                DEFAULT_UID,
                DEFAULT_EMAIL
            )
        } returns true
        
        viewModel.onMailAddressTextChanged(DEFAULT_EMAIL)
        viewModel.onValidateMailButtonClicked()
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(UserSettingsAction.ModificationsSuccess)
        }
    }
    
    @Test
    fun `nominal case - update email auth failure`() = testCoroutineRule.runTest {
        coEvery { updateAuthMailAddressUseCase.invoke(DEFAULT_EMAIL) } returns false
        coEvery {
            updateFirestoreMailAddressUseCase.invoke(
                DEFAULT_UID,
                DEFAULT_EMAIL
            )
        } returns true
        
        viewModel.onMailAddressTextChanged(DEFAULT_EMAIL)
        viewModel.onValidateMailButtonClicked()
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(UserSettingsAction.Error(R.string.error_occurred))
        }
    }
    
    @Test
    fun `nominal case - update email firestore failure`() = testCoroutineRule.runTest {
        coEvery { updateAuthMailAddressUseCase.invoke(DEFAULT_EMAIL) } returns true
        coEvery {
            updateFirestoreMailAddressUseCase.invoke(
                DEFAULT_UID,
                DEFAULT_EMAIL
            )
        } returns false
        
        viewModel.onMailAddressTextChanged(DEFAULT_EMAIL)
        viewModel.onValidateMailButtonClicked()
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(UserSettingsAction.Error(R.string.error_occurred))
        }
    }
    
    
    @Test
    fun `nominal case - update password null mail`() = testCoroutineRule.runTest {
        viewModel.onValidatePasswordButtonClicked()
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(UserSettingsAction.Error(R.string.error_password_empty))
        }
    }
    
    @Test
    fun `nominal case - update password empty mail`() = testCoroutineRule.runTest {
        viewModel.onPasswordTextChanged("")
        viewModel.onValidatePasswordButtonClicked()
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(UserSettingsAction.Error(R.string.error_password_empty))
        }
    }
    
    @Test
    fun `nominal case - update password success`() = testCoroutineRule.runTest {
        coEvery { updateAuthPasswordUseCase.invoke(DEFAULT_PASSWORD) } returns true
        
        
        viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
        viewModel.onValidatePasswordButtonClicked()
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(UserSettingsAction.ModificationsSuccess)
        }
    }
    
    @Test
    fun `nominal case - update password auth failure`() = testCoroutineRule.runTest {
        coEvery { updateAuthPasswordUseCase.invoke(DEFAULT_PASSWORD) } returns false
        
        viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
        viewModel.onValidatePasswordButtonClicked()
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(UserSettingsAction.Error(R.string.error_occurred))
        }
    }
    
    @Test
    fun `nominal case - switch changed success`() = testCoroutineRule.runTest {
        coEvery { updateNotificationStateUseCase.invoke(true) } returns true
        viewModel.onSwitchChanged(true)
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(UserSettingsAction.ModificationsSuccess)
        }
    }
    
    @Test
    fun `nominal case - switch changed failure`() = testCoroutineRule.runTest {
        coEvery { updateNotificationStateUseCase.invoke(true) } returns false
        viewModel.onSwitchChanged(true)
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(UserSettingsAction.Error(R.string.error_occurred))
        }
    }
    
    @Ignore
    @Test
    fun `nominal case - update user image success`() = testCoroutineRule.runTest {
        coEvery {
            updateUserImageThenGetLinkUseCase.invoke(
                DEFAULT_UID,
                DEFAULT_PICTURE_URI
            )
        } returns DEFAULT_PICTURE
        
        coEvery {
            updateUserImageUseCase.invoke(
                DEFAULT_UID,
                DEFAULT_PICTURE
            )
        } returns true
        
        viewModel.onUserImageChange(DEFAULT_PICTURE_URI)
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(UserSettingsAction.ModificationsSuccess)
        }
    }
    
    @Ignore
    @Test
    fun `nominal case - update user image error`() = testCoroutineRule.runTest {
        coEvery {
            updateUserImageThenGetLinkUseCase.invoke(
                DEFAULT_UID,
                DEFAULT_PICTURE_URI
            )
        } returns DEFAULT_PICTURE
        
        coEvery {
            updateUserImageUseCase.invoke(
                DEFAULT_UID,
                DEFAULT_PICTURE
            )
        } returns false
        
        viewModel.onUserImageChange(DEFAULT_PICTURE_URI)
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(UserSettingsAction.Error(R.string.error_occurred))
        }
    }
    
    //Region IN
    private fun provideUserPreferencesDomainEntity(state: NotificationsStateEnum): UserPreferencesDomainEntity {
        return when (state) {
            NotificationsStateEnum.ENABLED -> UserPreferencesDomainEntity(isNotificationsEnabled = DEFAULT_NOTIF_STATE_ENABLED)
            NotificationsStateEnum.DISABLED -> UserPreferencesDomainEntity(isNotificationsEnabled = DEFAULT_NOTIF_STATE_DISABLED)
            NotificationsStateEnum.NOT_KNOW -> UserPreferencesDomainEntity(isNotificationsEnabled = DEFAULT_NOTIF_STATE_NOT_KNOW)
            else -> UserPreferencesDomainEntity(isNotificationsEnabled = DEFAULT_NOTIF_STATE_NOT_KNOW)
        }
    }
    
    private fun provideAuthenticatedUserEntity() = AuthenticateUserEntity(
        picture = DEFAULT_PICTURE,
        displayName = DEFAULT_DISPLAY_NAME,
        mailAddress = DEFAULT_EMAIL,
        uid = DEFAULT_UID,
        currentlyEating = DEFAULT_CURRENTLY_EATING,
        eatingPlaceId = DEFAULT_EATING_PLACE_IDE
    )
    //End Region IN
}