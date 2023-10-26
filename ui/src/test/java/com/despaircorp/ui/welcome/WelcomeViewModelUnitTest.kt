package com.despaircorp.ui.welcome

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.firebaseAuth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firestore.GetFirestoreUserUseCase
import com.despaircorp.ui.utils.TestCoroutineRule
import com.despaircorp.ui.utils.observeForTesting
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.delay
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class WelcomeViewModelUnitTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase = mockk()
    private val getFirestoreUserUseCase: GetFirestoreUserUseCase = mockk()
    
    private lateinit var viewModel: WelcomeViewModel
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
    }
    
    @Before
    fun setup() {
        coEvery { getAuthenticatedUserUseCase.invoke().uid } returns DEFAULT_UID
        coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
        
        viewModel = WelcomeViewModel(
            getAuthenticatedUserUseCase = getAuthenticatedUserUseCase,
            getFirestoreUserUseCase = getFirestoreUserUseCase
        )
    }
    
    @Test
    fun `nominal case - get display name and go next`() = testCoroutineRule.runTest {
        viewModel.viewState.observeForTesting(this) {
            assertThat(it.value).isEqualTo(
                WelcomeViewState(
                    username = DEFAULT_DISPLAY_NAME
                )
            )
        }
        delay(2.seconds)
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(WelcomeViewAction.Continue)
        }
    }
}