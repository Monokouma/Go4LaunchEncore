package com.despaircorp.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.firebaseAuth.CreateCredentialsUserUseCase
import com.despaircorp.domain.firebaseAuth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firebaseAuth.IsUserAlreadyAuthUseCase
import com.despaircorp.domain.firebaseAuth.IsUserWithCredentialsSignedInUseCase
import com.despaircorp.domain.firebaseAuth.SignInTokenUserUseCase
import com.despaircorp.domain.firebaseAuth.model.AuthenticateUserEntity
import com.despaircorp.domain.firestore.GetFirestoreUserUseCase
import com.despaircorp.domain.firestore.InsertUserInFirestoreUseCase
import com.despaircorp.domain.firestore.IsFirestoreUserExistUseCase
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.TestCoroutineRule
import com.despaircorp.ui.utils.observeForTesting
import com.facebook.AccessToken
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Before
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
    
    private lateinit var viewModel: LoginViewModel
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
        private const val DEFAULT_MAIL = "DEFAULT_MAIL"
        private const val DEFAULT_PASSWORD = "DEFAULT_PASSWORD"
        private const val DEFAULT_PICTURE = "DEFAULT_PICTURE"
        private const val DEFAULT_TOKEN = "DEFAULT_TOKEN"
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
        
        viewModel = LoginViewModel(
            signInTokenUserUseCase = signInTokenUserUseCase,
            isFirestoreUserExistUseCase = isFirestoreUserExistUseCase,
            getAuthenticatedUserUseCase = getAuthenticatedUserUseCase,
            getFirestoreUserUseCase = getFirestoreUserUseCase,
            insertUserInFirestoreUseCase = insertUserInFirestoreUseCase,
            isUserAlreadyAuthUseCase = isUserAlreadyAuthUseCase,
            isUserWithCredentialsSignedInUseCase = isUserWithCredentialsSignedInUseCase,
            createCredentialsUserUseCase = createCredentialsUserUseCase
        )
    }
    
    @Test
    fun `nominal case - auth with facebook existing user`() = testCoroutineRule.runTest {
        coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns true
        
        viewModel.onFacebookConnection(accessToken)
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.peekContent()).isEqualTo(LoginAction.GoToWelcome)
        }
    }
    
    @Test
    fun `edge case - auth with facebook existing user with none name`() =
        testCoroutineRule.runTest {
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns "none"
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns true
            
            viewModel.onFacebookConnection(accessToken)
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.peekContent()).isEqualTo(LoginAction.ChoseUsername)
            }
        }
    
    @Test
    fun `edge case - auth with facebook unexisting user`() = testCoroutineRule.runTest {
        viewModel.onFacebookConnection(accessToken)
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.peekContent()).isEqualTo(LoginAction.GoToWelcome)
        }
    }
    
    @Test
    fun `error case - auth with facebook failure`() = testCoroutineRule.runTest {
        coEvery { signInTokenUserUseCase.invoke(accessToken) } returns false
        
        viewModel.onFacebookConnection(accessToken)
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.peekContent()).isEqualTo(LoginAction.Error(R.string.error_occurred))
        }
    }
    
    @Test
    fun `error case - auth with facebook insertion failure`() = testCoroutineRule.runTest {
        coEvery { insertUserInFirestoreUseCase.invoke(provideAuthenticatedUserEntity()) } returns false
        
        viewModel.onFacebookConnection(accessToken)
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.peekContent()).isEqualTo(LoginAction.Error(R.string.error_occurred))
        }
    }
    
    @Test
    fun `edge case - auth with facebook not existing user with none name`() =
        testCoroutineRule.runTest {
            coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns "none"
            coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns false
            
            viewModel.onFacebookConnection(accessToken)
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.peekContent()).isEqualTo(LoginAction.ChoseUsername)
            }
        }
    
    @Test
    fun `nominal case - already auth user`() = testCoroutineRule.runTest {
        coEvery { isUserAlreadyAuthUseCase.invoke() } returns true
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.peekContent()).isEqualTo(LoginAction.GoToWelcome)
        }
    }
    
    @Test
    fun `error case - already auth user insertion error`() = testCoroutineRule.runTest {
        coEvery { isUserAlreadyAuthUseCase.invoke() } returns true
        coEvery { insertUserInFirestoreUseCase.invoke(provideAuthenticatedUserEntity()) } returns false
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.peekContent()).isEqualTo(LoginAction.Error(R.string.error_occurred))
        }
    }
    
    @Test
    fun `edge case - already auth user display name`() = testCoroutineRule.runTest {
        coEvery { isUserAlreadyAuthUseCase.invoke() } returns true
        coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns "none"
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.peekContent()).isEqualTo(LoginAction.ChoseUsername)
        }
    }
    
    @Test
    fun `edge case - already auth user exist`() = testCoroutineRule.runTest {
        coEvery { isUserAlreadyAuthUseCase.invoke() } returns true
        coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns true
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.peekContent()).isEqualTo(LoginAction.GoToWelcome)
        }
    }
    
    @Test
    fun `edge case - already auth user exist none display name`() = testCoroutineRule.runTest {
        coEvery { isUserAlreadyAuthUseCase.invoke() } returns true
        coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns true
        coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns "none"
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.peekContent()).isEqualTo(LoginAction.ChoseUsername)
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
                assertThat(it.value?.peekContent()).isEqualTo(LoginAction.ChoseUsername)
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
                assertThat(it.value?.peekContent()).isEqualTo(LoginAction.Error(R.string.error_occurred))
            }
        }
    
    @Test
    fun `edge case - user register with credential mail null value`() =
        testCoroutineRule.runTest {
            viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.peekContent()).isEqualTo(LoginAction.Error(R.string.error_credentials_empty))
            }
        }
    
    @Test
    fun `edge case - user register with credential password null value`() =
        testCoroutineRule.runTest {
            viewModel.onMailTextChanged(DEFAULT_MAIL)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.peekContent()).isEqualTo(LoginAction.Error(R.string.error_credentials_empty))
            }
        }
    
    @Test
    fun `edge case - user register with credential mail empty value`() =
        testCoroutineRule.runTest {
            viewModel.onMailTextChanged("")
            viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.peekContent()).isEqualTo(LoginAction.Error(R.string.error_credentials_empty))
            }
        }
    
    @Test
    fun `edge case - user register with credential password empty value`() =
        testCoroutineRule.runTest {
            viewModel.onMailTextChanged(DEFAULT_MAIL)
            viewModel.onPasswordTextChanged("")
            viewModel.onConnectWithCredentialsClicked()
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.peekContent()).isEqualTo(LoginAction.Error(R.string.error_credentials_empty))
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
                assertThat(it.value?.peekContent()).isEqualTo(LoginAction.Error(R.string.error_occurred))
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
                assertThat(it.value?.peekContent()).isEqualTo(LoginAction.GoToWelcome)
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
                assertThat(it.value?.peekContent()).isEqualTo(LoginAction.ChoseUsername)
            }
        }
    
    @Test
    fun `nominal case user is already signed go to welcome have name`() =
        testCoroutineRule.runTest {
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
                assertThat(it.value?.peekContent()).isEqualTo(LoginAction.GoToWelcome)
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
                assertThat(it.value?.peekContent()).isEqualTo(LoginAction.Error(R.string.error_occurred))
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
        coEvery { isFirestoreUserExistUseCase.invoke(DEFAULT_UID) } returns true
        viewModel.onPasswordTextChanged(DEFAULT_PASSWORD)
        viewModel.onMailTextChanged(DEFAULT_MAIL)
        viewModel.onConnectWithCredentialsClicked()
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.peekContent()).isEqualTo(LoginAction.GoToWelcome)
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
                assertThat(it.value?.peekContent()).isEqualTo(LoginAction.ChoseUsername)
            }
            
        }
    
    private fun provideAuthenticatedUserEntity() = AuthenticateUserEntity(
        picture = DEFAULT_PICTURE,
        displayName = DEFAULT_DISPLAY_NAME,
        mailAddress = DEFAULT_MAIL,
        uid = DEFAULT_UID
    )
}