package com.despaircorp.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.firebase_auth.CreateCredentialsUserUseCase
import com.despaircorp.domain.firebase_auth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firebase_auth.GetCurrentFacebookAccessTokenUseCase
import com.despaircorp.domain.firebase_auth.IsUserAlreadyAuthUseCase
import com.despaircorp.domain.firebase_auth.IsUserWithCredentialsSignedInUseCase
import com.despaircorp.domain.firebase_auth.SignInTokenUserUseCase
import com.despaircorp.domain.firebase_auth.model.AuthenticateUserEntity
import com.despaircorp.domain.firestore.GetFirestoreUserUseCase
import com.despaircorp.domain.firestore.InsertUserInFirestoreUseCase
import com.despaircorp.domain.firestore.IsFirestoreUserExistUseCase
import com.despaircorp.domain.room.InitUserPreferencesUseCase
import com.despaircorp.domain.room.IsNotificationsEnabledUseCase
import com.despaircorp.domain.room.IsUserPreferencesTableExistUseCase
import com.despaircorp.domain.room.model.NotificationsStateEnum
import com.despaircorp.domain.room.model.UserPreferencesDomainEntity
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.TestCoroutineRule
import com.despaircorp.ui.utils.observeForTesting
import com.facebook.AccessToken
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val signInTokenUserUseCase: SignInTokenUserUseCase = mockk()
    private val isFirestoreUserExistUseCase: IsFirestoreUserExistUseCase = mockk()
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase = mockk()
    private val getFirestoreUserUseCase: GetFirestoreUserUseCase = mockk()
    private val insertUserInFirestoreUseCase: InsertUserInFirestoreUseCase = mockk()
    private val isUserAlreadyAuthUseCase: IsUserAlreadyAuthUseCase = mockk()
    private val isUserWithCredentialsSignedInUseCase: IsUserWithCredentialsSignedInUseCase = mockk()
    private val createCredentialsUserUseCase: CreateCredentialsUserUseCase = mockk()
    private val accessToken: AccessToken = mockk()
    private val isNotificationsEnabledUseCase: IsNotificationsEnabledUseCase = mockk()
    private val initUserPreferencesUseCase: InitUserPreferencesUseCase = mockk()
    private val isUserPreferencesTableExistUseCase: IsUserPreferencesTableExistUseCase = mockk()
    private val getCurrentFacebookAccessToken: GetCurrentFacebookAccessTokenUseCase =
        mockk()
    
    private lateinit var viewModel: LoginViewModel
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
        private const val DEFAULT_MAIL = "DEFAULT_MAIL"
        private const val DEFAULT_PASSWORD = "DEFAULT_PASSWORD"
        private const val DEFAULT_PICTURE = "DEFAULT_PICTURE"
        private const val DEFAULT_TOKEN = "DEFAULT_TOKEN"
        private const val DEFAULT_CURRENTLY_EATING = false
        private val DEFAULT_EATING_PLACE_IDE = null
        
        private const val DEFAULT_PICTURE_FACEBOOK =
            "https://graph.facebook.com${DEFAULT_PICTURE}?type=large&access_token=${DEFAULT_TOKEN}"
        
        private val DEFAULT_NOTIF_STATE_NOT_KNOW = NotificationsStateEnum.NOT_KNOW
        private val DEFAULT_NOTIF_STATE_ENABLED = NotificationsStateEnum.ENABLED
        private val DEFAULT_NOTIF_STATE_DISABLED = NotificationsStateEnum.DISABLED
    }
    
    @Before
    fun setup() {
        
        coEvery { isUserAlreadyAuthUseCase.invoke() } returns false
        coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns false
        coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
        coEvery { insertUserInFirestoreUseCase.invoke(provideAuthenticatedUserEntity()) } returns true
        coEvery {
            isUserWithCredentialsSignedInUseCase.invoke(
                DEFAULT_MAIL,
                DEFAULT_PASSWORD
            )
        } returns false
        coEvery { createCredentialsUserUseCase.invoke(DEFAULT_MAIL, DEFAULT_PASSWORD) } returns true
        coEvery { signInTokenUserUseCase.invoke(accessToken) } returns true
        coEvery { getAuthenticatedUserUseCase.invoke() } returns provideAuthenticatedUserEntity()
        every { getCurrentFacebookAccessToken.invoke() } returns DEFAULT_TOKEN
        
        viewModel = LoginViewModel(
            signInTokenUserUseCase = signInTokenUserUseCase,
            isFirestoreUserExistUseCase = isFirestoreUserExistUseCase,
            getAuthenticatedUserUseCase = getAuthenticatedUserUseCase,
            getFirestoreUserUseCase = getFirestoreUserUseCase,
            insertUserInFirestoreUseCase = insertUserInFirestoreUseCase,
            isUserAlreadyAuthUseCase = isUserAlreadyAuthUseCase,
            isUserWithCredentialsSignedInUseCase = isUserWithCredentialsSignedInUseCase,
            createCredentialsUserUseCase = createCredentialsUserUseCase,
            isNotificationsEnabledUseCase = isNotificationsEnabledUseCase,
            initUserPreferencesUseCase = initUserPreferencesUseCase,
            isUserPreferencesTableExistUseCase = isUserPreferencesTableExistUseCase,
            getCurrentFacebookAccessToken = getCurrentFacebookAccessToken,
        )
    }
    
    @Test
    fun `nominal case - auth with facebook existing user`() = testCoroutineRule.runTest {
        coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns true
        coEvery { isUserPreferencesTableExistUseCase.invoke() } returns true
        coEvery { isNotificationsEnabledUseCase.invoke() } returns UserPreferencesDomainEntity(
            NotificationsStateEnum.ENABLED
        )
        viewModel.onFacebookConnection(accessToken)
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.GoToWelcome)
        }
    }
    
    @Test
    fun `edge case - auth with facebook existing user with none name`() =
        testCoroutineRule.runTest {
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns "none"
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns true
            
            viewModel.onFacebookConnection(accessToken)
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.ChoseUsername)
            }
        }
    
    @Ignore
    @Test
    fun `edge case - auth with facebook unexisting user`() = testCoroutineRule.runTest {
        coEvery { isUserPreferencesTableExistUseCase.invoke() } returns true
        coEvery { isNotificationsEnabledUseCase.invoke() } returns UserPreferencesDomainEntity(
            NotificationsStateEnum.ENABLED
        )
        
        viewModel.onFacebookConnection(accessToken)
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.GoToWelcome)
        }
    }
    
    @Test
    fun `error case - auth with facebook failure`() = testCoroutineRule.runTest {
        coEvery { signInTokenUserUseCase.invoke(accessToken) } returns false
        
        viewModel.onFacebookConnection(accessToken)
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.Error(R.string.error_occurred))
        }
    }
    
    @Test
    fun `error case - auth with facebook insertion failure`() = testCoroutineRule.runTest {
        coEvery {
            insertUserInFirestoreUseCase.invoke(
                provideAuthenticatedUserEntity().copy(
                    picture = DEFAULT_PICTURE_FACEBOOK,
                )
            )
        } returns false
        
        viewModel.onFacebookConnection(accessToken)
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.Error(R.string.error_occurred))
        }
    }
    
    @Ignore
    @Test
    fun `edge case - auth with facebook not existing user with none name`() =
        testCoroutineRule.runTest {
            
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns "none"
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns false
            
            viewModel.onFacebookConnection(accessToken)
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.ChoseUsername)
            }
        }
    
    @Ignore
    @Test
    fun `nominal case - already auth user`() = testCoroutineRule.runTest {
        coEvery { isUserAlreadyAuthUseCase.invoke() } returns true
        coEvery { isUserPreferencesTableExistUseCase.invoke() } returns true
        coEvery { isNotificationsEnabledUseCase.invoke() } returns UserPreferencesDomainEntity(
            NotificationsStateEnum.ENABLED
        )
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.GoToWelcome)
        }
    }
    
    @Ignore
    @Test
    fun `error case - already auth user insertion error`() = testCoroutineRule.runTest {
        coEvery { isUserAlreadyAuthUseCase.invoke() } returns true
        coEvery { insertUserInFirestoreUseCase.invoke(provideAuthenticatedUserEntity()) } returns false
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.Error(R.string.error_occurred))
        }
    }
    
    @Ignore
    @Test
    fun `edge case - already auth user display name`() = testCoroutineRule.runTest {
        coEvery { isUserAlreadyAuthUseCase.invoke() } returns true
        coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns "none"
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.ChoseUsername)
        }
    }
    
    @Ignore
    @Test
    fun `edge case - already auth user exist`() = testCoroutineRule.runTest {
        coEvery { isUserPreferencesTableExistUseCase.invoke() } returns true
        coEvery { isNotificationsEnabledUseCase.invoke() } returns UserPreferencesDomainEntity(
            NotificationsStateEnum.ENABLED
        )
        coEvery { isUserAlreadyAuthUseCase.invoke() } returns true
        coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns true
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.GoToWelcome)
        }
    }
    
    @Ignore
    @Test
    fun `edge case - already auth user exist none display name`() = testCoroutineRule.runTest {
        coEvery { isUserAlreadyAuthUseCase.invoke() } returns true
        coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns true
        coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns "none"
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.ChoseUsername)
        }
    }
    
    @Test
    fun `nominal case - user register with credential success un existing user`() =
        testCoroutineRule.runTest {
            coEvery {
                isUserWithCredentialsSignedInUseCase.invoke(
                    DEFAULT_MAIL,
                    DEFAULT_PASSWORD
                )
            } returns false
            coEvery {
                createCredentialsUserUseCase.invoke(
                    DEFAULT_MAIL,
                    DEFAULT_PASSWORD
                )
            } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns "none"
            
            viewModel.onMailTextChanged(DEFAULT_MAIL)
            viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.ChoseUsername)
            }
        }
    
    @Test
    fun `edge case - user register with credential success un existing user error`() =
        testCoroutineRule.runTest {
            coEvery {
                isUserWithCredentialsSignedInUseCase.invoke(
                    DEFAULT_MAIL,
                    DEFAULT_PASSWORD
                )
            } returns false
            coEvery {
                createCredentialsUserUseCase.invoke(
                    DEFAULT_MAIL,
                    DEFAULT_PASSWORD
                )
            } returns false
            
            viewModel.onMailTextChanged(DEFAULT_MAIL)
            viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.Error(R.string.error_occurred))
            }
        }
    
    @Test
    fun `edge case - user register with credential mail null value`() =
        testCoroutineRule.runTest {
            viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.Error(R.string.error_credentials_empty))
            }
        }
    
    @Test
    fun `edge case - user register with credential password null value`() =
        testCoroutineRule.runTest {
            viewModel.onMailTextChanged(DEFAULT_MAIL)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.Error(R.string.error_credentials_empty))
            }
        }
    
    @Test
    fun `edge case - user register with credential mail empty value`() =
        testCoroutineRule.runTest {
            viewModel.onMailTextChanged("")
            viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.Error(R.string.error_credentials_empty))
            }
        }
    
    @Test
    fun `edge case - user register with credential password empty value`() =
        testCoroutineRule.runTest {
            viewModel.onMailTextChanged(DEFAULT_MAIL)
            viewModel.onPasswordTextChanged("")
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.Error(R.string.error_credentials_empty))
            }
        }
    
    @Test
    fun `edge case - user register with credential insertion error`() =
        testCoroutineRule.runTest {
            coEvery {
                isUserWithCredentialsSignedInUseCase.invoke(
                    DEFAULT_MAIL,
                    DEFAULT_PASSWORD
                )
            } returns false
            coEvery {
                createCredentialsUserUseCase.invoke(
                    DEFAULT_MAIL,
                    DEFAULT_PASSWORD
                )
            } returns true
            
            coEvery { insertUserInFirestoreUseCase.invoke(provideAuthenticatedUserEntity()) } returns false
            
            viewModel.onMailTextChanged(DEFAULT_MAIL)
            viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.Error(R.string.error_occurred))
            }
        }
    
    @Test
    fun `edge case - user register with credential none display name`() =
        testCoroutineRule.runTest {
            coEvery {
                isUserWithCredentialsSignedInUseCase.invoke(
                    DEFAULT_MAIL,
                    DEFAULT_PASSWORD
                )
            } returns false
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns true
            coEvery { isNotificationsEnabledUseCase.invoke() } returns UserPreferencesDomainEntity(
                NotificationsStateEnum.ENABLED
            )
            coEvery {
                createCredentialsUserUseCase.invoke(
                    DEFAULT_MAIL,
                    DEFAULT_PASSWORD
                )
            } returns true
            
            coEvery { insertUserInFirestoreUseCase.invoke(provideAuthenticatedUserEntity()) } returns true
            
            
            viewModel.onMailTextChanged(DEFAULT_MAIL)
            viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.GoToWelcome)
            }
        }
    
    @Test
    fun `nominal case user is already signed go to welcome display name none`() =
        testCoroutineRule.runTest {
            coEvery {
                isUserWithCredentialsSignedInUseCase.invoke(
                    DEFAULT_MAIL,
                    DEFAULT_PASSWORD
                )
            } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns "none"
            
            viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
            viewModel.onMailTextChanged(DEFAULT_MAIL)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.ChoseUsername)
            }
        }
    
    @Test
    fun `nominal case user is already signed go to welcome have name`() =
        testCoroutineRule.runTest {
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns true
            coEvery { isNotificationsEnabledUseCase.invoke() } returns UserPreferencesDomainEntity(
                NotificationsStateEnum.ENABLED
            )
            coEvery {
                isUserWithCredentialsSignedInUseCase.invoke(
                    DEFAULT_MAIL,
                    DEFAULT_PASSWORD
                )
            } returns true
            
            viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
            viewModel.onMailTextChanged(DEFAULT_MAIL)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.GoToWelcome)
            }
        }
    
    @Test
    fun `nominal case user is already signed go to welcome have name insertion error`() =
        testCoroutineRule.runTest {
            coEvery {
                isUserWithCredentialsSignedInUseCase.invoke(
                    DEFAULT_MAIL,
                    DEFAULT_PASSWORD
                )
            } returns true
            coEvery { insertUserInFirestoreUseCase.invoke(provideAuthenticatedUserEntity()) } returns false
            
            viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
            viewModel.onMailTextChanged(DEFAULT_MAIL)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.Error(R.string.error_occurred))
            }
        }
    
    @Test
    fun `nominal case user is already signed existing user`() = testCoroutineRule.runTest {
        coEvery {
            isUserWithCredentialsSignedInUseCase.invoke(
                DEFAULT_MAIL,
                DEFAULT_PASSWORD
            )
        } returns true
        coEvery { isUserPreferencesTableExistUseCase.invoke() } returns true
        coEvery { isNotificationsEnabledUseCase.invoke() } returns UserPreferencesDomainEntity(
            NotificationsStateEnum.ENABLED
        )
        coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns true
        viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
        viewModel.onMailTextChanged(DEFAULT_MAIL)
        viewModel.onConnectWithCredentialsClicked()
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.GoToWelcome)
        }
        
    }
    
    @Test
    fun `nominal case user is already signed existing user display name none`() =
        testCoroutineRule.runTest {
            coEvery {
                isUserWithCredentialsSignedInUseCase.invoke(
                    DEFAULT_MAIL,
                    DEFAULT_PASSWORD
                )
            } returns true
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns "none"
            
            viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
            viewModel.onMailTextChanged(DEFAULT_MAIL)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.ChoseUsername)
            }
        }
    
    @Test
    fun `nominal case user is already signed existing user and not know notif`() =
        testCoroutineRule.runTest {
            coEvery {
                isUserWithCredentialsSignedInUseCase.invoke(
                    DEFAULT_MAIL,
                    DEFAULT_PASSWORD
                )
            } returns true
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns true
            coEvery { isNotificationsEnabledUseCase.invoke().isNotificationsEnabled } returns DEFAULT_NOTIF_STATE_NOT_KNOW
            
            
            viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
            viewModel.onMailTextChanged(DEFAULT_MAIL)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.EnableNotifications)
            }
        }
    
    @Ignore
    @Test
    fun `nominal case - already auth user with display name and notif not know`() =
        testCoroutineRule.runTest {
            coEvery { isUserAlreadyAuthUseCase.invoke() } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns true
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns true
            coEvery { isNotificationsEnabledUseCase.invoke() } returns provideUserPreferencesDomainEntity(
                DEFAULT_NOTIF_STATE_NOT_KNOW
            )
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(
                    LoginAction.EnableNotifications
                )
            }
        }
    
    @Ignore
    @Test
    fun `nominal case - already auth user with display name and unexisting table`() =
        testCoroutineRule.runTest {
            coEvery { isUserAlreadyAuthUseCase.invoke() } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns true
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns false
            coEvery { isNotificationsEnabledUseCase.invoke() } returns provideUserPreferencesDomainEntity(
                DEFAULT_NOTIF_STATE_NOT_KNOW
            )
            coEvery {
                initUserPreferencesUseCase.invoke(
                    provideUserPreferencesDomainEntity(
                        DEFAULT_NOTIF_STATE_NOT_KNOW
                    )
                )
            } returns true
            
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(
                    LoginAction.EnableNotifications
                )
            }
        }
    
    @Ignore
    @Test
    fun `error case - already auth user with display name and unexisting table`() =
        testCoroutineRule.runTest {
            coEvery { isUserAlreadyAuthUseCase.invoke() } returns true
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns false
            
            coEvery {
                initUserPreferencesUseCase.invoke(
                    provideUserPreferencesDomainEntity(
                        DEFAULT_NOTIF_STATE_NOT_KNOW
                    )
                )
            } returns false
            
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(
                    LoginAction.Error(R.string.error_occurred)
                )
            }
        }
    
    @Ignore
    @Test
    fun `nominal case - not auth user with display name and notif not know`() =
        testCoroutineRule.runTest {
            coEvery { isUserAlreadyAuthUseCase.invoke() } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns false
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns true
            coEvery { isNotificationsEnabledUseCase.invoke() } returns provideUserPreferencesDomainEntity(
                DEFAULT_NOTIF_STATE_NOT_KNOW
            )
            coEvery { insertUserInFirestoreUseCase.invoke(provideAuthenticatedUserEntity()) } returns true
            
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(
                    LoginAction.EnableNotifications
                )
            }
        }
    
    @Ignore
    @Test
    fun `nominal case - not auth user with display name and unexisting table`() =
        testCoroutineRule.runTest {
            coEvery { isUserAlreadyAuthUseCase.invoke() } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns false
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns false
            coEvery { isNotificationsEnabledUseCase.invoke() } returns provideUserPreferencesDomainEntity(
                DEFAULT_NOTIF_STATE_NOT_KNOW
            )
            coEvery { insertUserInFirestoreUseCase.invoke(provideAuthenticatedUserEntity()) } returns true
            
            coEvery {
                initUserPreferencesUseCase.invoke(
                    provideUserPreferencesDomainEntity(
                        DEFAULT_NOTIF_STATE_NOT_KNOW
                    )
                )
            } returns true
            
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(
                    LoginAction.EnableNotifications
                )
            }
        }
    
    @Ignore
    @Test
    fun `error case - not auth user with display name and unexisting table`() =
        testCoroutineRule.runTest {
            coEvery { isUserAlreadyAuthUseCase.invoke() } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns false
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns false
            coEvery { isNotificationsEnabledUseCase.invoke() } returns provideUserPreferencesDomainEntity(
                DEFAULT_NOTIF_STATE_NOT_KNOW
            )
            coEvery { insertUserInFirestoreUseCase.invoke(provideAuthenticatedUserEntity()) } returns true
            coEvery {
                initUserPreferencesUseCase.invoke(
                    provideUserPreferencesDomainEntity(
                        DEFAULT_NOTIF_STATE_NOT_KNOW
                    )
                )
            } returns false
            
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(
                    LoginAction.Error(R.string.error_occurred)
                )
            }
        }
    
    @Test
    fun `nominal case - on connect with credentials and unexsisting table`() =
        testCoroutineRule.runTest {
            coEvery {
                isUserWithCredentialsSignedInUseCase.invoke(
                    DEFAULT_MAIL,
                    DEFAULT_PASSWORD
                )
            } returns true
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns false
            coEvery {
                initUserPreferencesUseCase.invoke(
                    provideUserPreferencesDomainEntity(
                        DEFAULT_NOTIF_STATE_NOT_KNOW
                    )
                )
            } returns true
            
            
            viewModel.onMailTextChanged(DEFAULT_MAIL)
            viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(
                    LoginAction.EnableNotifications
                )
            }
        }
    
    @Test
    fun `error case - on connect with credentials and unexsisting table`() =
        testCoroutineRule.runTest {
            coEvery {
                isUserWithCredentialsSignedInUseCase.invoke(
                    DEFAULT_MAIL,
                    DEFAULT_PASSWORD
                )
            } returns true
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns false
            coEvery {
                initUserPreferencesUseCase.invoke(
                    provideUserPreferencesDomainEntity(
                        DEFAULT_NOTIF_STATE_NOT_KNOW
                    )
                )
            } returns false
            
            
            viewModel.onMailTextChanged(DEFAULT_MAIL)
            viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(
                    LoginAction.Error(R.string.error_occurred)
                )
            }
        }
    
    
    @Test
    fun `nominal case - on connect with credentials and unexisting user and unexsisting table`() =
        testCoroutineRule.runTest {
            coEvery {
                isUserWithCredentialsSignedInUseCase.invoke(
                    DEFAULT_MAIL,
                    DEFAULT_PASSWORD
                )
            } returns true
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns false
            coEvery { insertUserInFirestoreUseCase.invoke(provideAuthenticatedUserEntity()) } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns false
            coEvery {
                initUserPreferencesUseCase.invoke(
                    provideUserPreferencesDomainEntity(
                        DEFAULT_NOTIF_STATE_NOT_KNOW
                    )
                )
            } returns true
            coEvery { isNotificationsEnabledUseCase.invoke().isNotificationsEnabled } returns DEFAULT_NOTIF_STATE_NOT_KNOW
            
            viewModel.onMailTextChanged(DEFAULT_MAIL)
            viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(
                    LoginAction.EnableNotifications
                )
            }
        }
    
    @Test
    fun `nominal case - singed in user with credentials not created in firestore with existing preferences table`() =
        testCoroutineRule.runTest {
            coEvery {
                isUserWithCredentialsSignedInUseCase.invoke(
                    DEFAULT_MAIL,
                    DEFAULT_PASSWORD
                )
            } returns true
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns false
            coEvery { insertUserInFirestoreUseCase.invoke(provideAuthenticatedUserEntity()) } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns true
            coEvery { isNotificationsEnabledUseCase.invoke().isNotificationsEnabled } returns DEFAULT_NOTIF_STATE_NOT_KNOW
            
            viewModel.onMailTextChanged(DEFAULT_MAIL)
            viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(
                    LoginAction.EnableNotifications
                )
            }
        }
    
    @Test
    fun `error case - singed in user with credentials not created in firestore with unexisting preferences table`() =
        testCoroutineRule.runTest {
            coEvery {
                isUserWithCredentialsSignedInUseCase.invoke(
                    DEFAULT_MAIL,
                    DEFAULT_PASSWORD
                )
            } returns true
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns false
            coEvery { insertUserInFirestoreUseCase.invoke(provideAuthenticatedUserEntity()) } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns false
            coEvery {
                initUserPreferencesUseCase.invoke(
                    provideUserPreferencesDomainEntity(
                        DEFAULT_NOTIF_STATE_NOT_KNOW
                    )
                )
            } returns false
            
            viewModel.onMailTextChanged(DEFAULT_MAIL)
            viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(
                    LoginAction.Error(R.string.error_occurred)
                )
            }
        }
    
    @Test
    fun `nominal case - user not signed in with display name with table created and not know notifications state`() =
        testCoroutineRule.runTest {
            coEvery {
                isUserWithCredentialsSignedInUseCase.invoke(
                    DEFAULT_MAIL,
                    DEFAULT_UID
                )
            } returns false
            coEvery { createCredentialsUserUseCase.invoke(DEFAULT_MAIL, DEFAULT_UID) } returns true
            coEvery { insertUserInFirestoreUseCase.invoke(provideAuthenticatedUserEntity()) } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns true
            coEvery { isNotificationsEnabledUseCase.invoke().isNotificationsEnabled } returns DEFAULT_NOTIF_STATE_NOT_KNOW
            
            viewModel.onMailTextChanged(DEFAULT_MAIL)
            viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.EnableNotifications)
            }
        }
    
    @Test
    fun `nominal case - user not signed in with display name with table not created and not know notifications state`() =
        testCoroutineRule.runTest {
            coEvery {
                isUserWithCredentialsSignedInUseCase.invoke(
                    DEFAULT_MAIL,
                    DEFAULT_UID
                )
            } returns false
            coEvery { createCredentialsUserUseCase.invoke(DEFAULT_MAIL, DEFAULT_UID) } returns true
            coEvery { insertUserInFirestoreUseCase.invoke(provideAuthenticatedUserEntity()) } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns false
            coEvery {
                initUserPreferencesUseCase.invoke(
                    provideUserPreferencesDomainEntity(
                        DEFAULT_NOTIF_STATE_NOT_KNOW
                    )
                )
            } returns true
            
            viewModel.onMailTextChanged(DEFAULT_MAIL)
            viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.EnableNotifications)
            }
        }
    
    @Test
    fun `nominal case - facebook user signed in with account and display name with table created and notification state not know`() =
        testCoroutineRule.runTest {
            coEvery { signInTokenUserUseCase.invoke(accessToken) } returns true
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns true
            coEvery { isNotificationsEnabledUseCase.invoke().isNotificationsEnabled } returns DEFAULT_NOTIF_STATE_NOT_KNOW
            
            viewModel.onFacebookConnection(accessToken)
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.EnableNotifications)
                
            }
        }
    
    @Test
    fun `nominal case - facebook user signed in with account and display name with table not created and notification state not know`() =
        testCoroutineRule.runTest {
            coEvery { signInTokenUserUseCase.invoke(accessToken) } returns true
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns false
            coEvery {
                initUserPreferencesUseCase.invoke(
                    provideUserPreferencesDomainEntity(
                        DEFAULT_NOTIF_STATE_NOT_KNOW
                    )
                )
            } returns true
            
            viewModel.onFacebookConnection(accessToken)
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.EnableNotifications)
                
            }
        }
    
    @Test
    fun `error case - facebook user signed in with account and display name with table not created and notification state not know`() =
        testCoroutineRule.runTest {
            coEvery { signInTokenUserUseCase.invoke(accessToken) } returns true
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns false
            coEvery {
                initUserPreferencesUseCase.invoke(
                    provideUserPreferencesDomainEntity(
                        DEFAULT_NOTIF_STATE_NOT_KNOW
                    )
                )
            } returns false
            
            viewModel.onFacebookConnection(accessToken)
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.Error(R.string.error_occurred))
                
            }
        }
    
    @Test
    fun `error case - user not signed in with display name with table not created and not know notifications state`() =
        testCoroutineRule.runTest {
            coEvery {
                isUserWithCredentialsSignedInUseCase.invoke(
                    DEFAULT_MAIL,
                    DEFAULT_UID
                )
            } returns false
            coEvery { createCredentialsUserUseCase.invoke(DEFAULT_MAIL, DEFAULT_UID) } returns true
            coEvery { insertUserInFirestoreUseCase.invoke(provideAuthenticatedUserEntity()) } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns false
            coEvery {
                initUserPreferencesUseCase.invoke(
                    provideUserPreferencesDomainEntity(
                        DEFAULT_NOTIF_STATE_NOT_KNOW
                    )
                )
            } returns false
            
            viewModel.onMailTextChanged(DEFAULT_MAIL)
            viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.Error(R.string.error_occurred))
            }
        }
    
    @Ignore
    @Test
    fun `nominal case - facebook user signed in without account and display name with table created and notification state not know`() =
        testCoroutineRule.runTest {
            coEvery { signInTokenUserUseCase.invoke(accessToken) } returns true
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns false
            coEvery { insertUserInFirestoreUseCase.invoke(provideAuthenticatedUserEntity()) } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns true
            coEvery { isNotificationsEnabledUseCase.invoke().isNotificationsEnabled } returns DEFAULT_NOTIF_STATE_NOT_KNOW
            
            
            viewModel.onFacebookConnection(accessToken)
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.EnableNotifications)
                
            }
        }
    
    @Test
    fun `nominal case - facebook user signed in without account and display name with table not created and notification state not know`() =
        testCoroutineRule.runTest {
            coEvery { signInTokenUserUseCase.invoke(accessToken) } returns true
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns false
            coEvery {
                insertUserInFirestoreUseCase.invoke(
                    provideAuthenticatedUserEntity().copy(
                        picture = DEFAULT_PICTURE_FACEBOOK,
                    )
                )
            } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns false
            coEvery {
                initUserPreferencesUseCase.invoke(
                    provideUserPreferencesDomainEntity(
                        DEFAULT_NOTIF_STATE_NOT_KNOW
                    )
                )
            } returns true
            
            viewModel.onFacebookConnection(accessToken)
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.EnableNotifications)
                
            }
        }
    
    @Ignore
    @Test
    fun `error case - facebook user signed in without account and display name with table not created and notification state not know`() =
        testCoroutineRule.runTest {
            coEvery { signInTokenUserUseCase.invoke(accessToken) } returns true
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns false
            coEvery { insertUserInFirestoreUseCase.invoke(provideAuthenticatedUserEntity()) } returns true
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
            coEvery { isUserPreferencesTableExistUseCase.invoke() } returns false
            coEvery {
                initUserPreferencesUseCase.invoke(
                    provideUserPreferencesDomainEntity(
                        DEFAULT_NOTIF_STATE_NOT_KNOW
                    )
                )
            } returns false
            
            viewModel.onFacebookConnection(accessToken)
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(LoginAction.Error(R.string.error_occurred))
                
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
    
    private fun provideAuthenticatedUserEntity() = AuthenticateUserEntity(
        picture = DEFAULT_PICTURE,
        displayName = DEFAULT_DISPLAY_NAME,
        mailAddress = DEFAULT_MAIL,
        uid = DEFAULT_UID,
        currentlyEating = DEFAULT_CURRENTLY_EATING,
        eatingPlaceId = DEFAULT_EATING_PLACE_IDE
    )
}