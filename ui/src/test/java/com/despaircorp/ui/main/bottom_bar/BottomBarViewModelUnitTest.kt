package com.despaircorp.ui.main.bottom_bar

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.firebaseAuth.DisconnectUserUseCase
import com.despaircorp.domain.firebaseAuth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firestore.GetFirestoreUserAsFlowUseCase
import com.despaircorp.domain.firestore.model.FirestoreUserEntity
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.TestCoroutineRule
import com.despaircorp.ui.utils.observeForTesting
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BottomBarViewModelUnitTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase = mockk()
    private val getFirestoreUserUseCase: GetFirestoreUserAsFlowUseCase = mockk()
    private val disconnectUserUseCase: DisconnectUserUseCase = mockk()
    
    private lateinit var viewModel: BottomBarViewModel
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
        private const val DEFAULT_EMAIL = "DEFAULT_EMAIL"
        private const val DEFAULT_PICTURE = "DEFAULT_PICTURE"
        
    }
    
    @Before
    fun setup() {
        coEvery { getAuthenticatedUserUseCase.invoke().uid } returns DEFAULT_UID
        coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID) } returns flowOf(
            FirestoreUserEntity(
                DEFAULT_PICTURE,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_EMAIL,
                DEFAULT_UID
            )
        )
        
        coEvery { disconnectUserUseCase.invoke() } returns true
        
        viewModel = BottomBarViewModel(
            getAuthenticatedUserUseCase = getAuthenticatedUserUseCase,
            disconnectUserUseCase = disconnectUserUseCase,
            getFirestoreUserAsFlowUseCase = getFirestoreUserUseCase,
        )
    }
    
    @Test
    fun `nominal case - infos are correct and sign out success`() = testCoroutineRule.runTest {
        viewModel.viewState.observeForTesting(this) {
            assertThat(it.value).isEqualTo(
                BottomBarViewState(
                    username = DEFAULT_DISPLAY_NAME,
                    emailAddress = DEFAULT_EMAIL,
                    userImage = DEFAULT_PICTURE
                )
            )
        }
        
        viewModel.onDisconnectUser()
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.peekContent()).isEqualTo(BottomBarAction.OnDisconnect)
        }
    }
    
    @Test
    fun `nominal case - infos are correct and sign out failure`() = testCoroutineRule.runTest {
        coEvery { disconnectUserUseCase.invoke() } returns false
        
        viewModel.viewState.observeForTesting(this) {
            assertThat(it.value).isEqualTo(
                BottomBarViewState(
                    username = DEFAULT_DISPLAY_NAME,
                    emailAddress = DEFAULT_EMAIL,
                    userImage = DEFAULT_PICTURE
                )
            )
        }
        
        viewModel.onDisconnectUser()
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.peekContent()).isEqualTo(BottomBarAction.Error(message = R.string.error_occurred))
        }
    }
}









