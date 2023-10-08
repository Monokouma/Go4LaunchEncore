package com.despaircorp.ui.username

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.firebaseAuth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firestore.UpdateUsernameUseCase
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
    
    private lateinit var viewModel: ChoseUsernameViewModel
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
    }
    
    @Before
    fun setup() {
        coEvery { getAuthenticatedUserUseCase.invoke().uid } returns DEFAULT_UID
        coEvery { updateUsernameUseCase.invoke(DEFAULT_DISPLAY_NAME, DEFAULT_UID) } returns true
        
        viewModel = ChoseUsernameViewModel(
            getAuthenticatedUserUseCase = getAuthenticatedUserUseCase,
            updateUsernameUseCase = updateUsernameUseCase
        )
    }
    
    @Test
    fun `nominal case - update username with success`() = testCoroutineRule.runTest {
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
}