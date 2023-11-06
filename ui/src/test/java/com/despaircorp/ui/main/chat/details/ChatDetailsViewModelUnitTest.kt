package com.despaircorp.ui.main.chat.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.firestore.GetFirestoreUserAsFlowUseCase
import com.despaircorp.domain.firestore.SendMessageUseCase
import com.despaircorp.domain.firestore.model.FirestoreUserEntity
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.TestCoroutineRule
import com.despaircorp.ui.utils.observeForTesting
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChatDetailsViewModelUnitTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val savedStateHandle: SavedStateHandle = mockk()
    private val getFirestoreUserAsFlowUseCase: GetFirestoreUserAsFlowUseCase = mockk()
    private val sendMessageUseCase: SendMessageUseCase = mockk()
    
    private lateinit var viewModel: ChatDetailsViewModel
    
    companion object {
        private const val ARG_TARGET_USER_UID = "ARG_TARGET_USER_UID"
        private const val DEFAULT_RECEIVER_UID = "DEFAULT_RECEIVER_UID"
        private const val DEFAULT_MESSAGE = "DEFAULT_MESSAGE"
        private const val DEFAULT_MAIL = "DEFAULT_MAIL"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
        private const val DEFAULT_PICTURE = "DEFAULT_PICTURE"
        private const val DEFAULT_CURRENTLY_EATING = false
        private val DEFAULT_EATING_PLACE_ID = null
        private const val DEFAULT_IS_ONLINE = true
    }
    
    @Before
    fun setup() {
        
        every { savedStateHandle.get<String>(ARG_TARGET_USER_UID) } returns DEFAULT_RECEIVER_UID
        coEvery { getFirestoreUserAsFlowUseCase.invoke(DEFAULT_RECEIVER_UID) } returns flowOf(
            provideFirestoreUserEntity()
        )
        coEvery { sendMessageUseCase.invoke(DEFAULT_RECEIVER_UID, DEFAULT_MESSAGE) } returns true
        
        viewModel = ChatDetailsViewModel(
            savedStateHandle = savedStateHandle,
            getFirestoreUserAsFlowUseCase = getFirestoreUserAsFlowUseCase,
            sendMessageUseCase = sendMessageUseCase
        )
    }
    
    @Test
    fun `initial case - show correct infos online user`() = testCoroutineRule.runTest {
        viewModel.viewState.observeForTesting(this) {
            assertThat(it.value).isEqualTo(provideChatDetailsViewState())
        }
    }
    
    @Test
    fun `initial case - show correct infos offline user`() = testCoroutineRule.runTest {
        coEvery { getFirestoreUserAsFlowUseCase.invoke(DEFAULT_RECEIVER_UID) } returns flowOf(
            provideFirestoreUserEntity().copy(online = false)
        )
        
        viewModel.viewState.observeForTesting(this) {
            assertThat(it.value).isEqualTo(provideChatDetailsViewState().copy(onlineDotResources = R.drawable.gray_circle))
        }
    }
    
    @Test
    fun `initial case - show correct infos offline user and ready to send message`() =
        testCoroutineRule.runTest {
            coEvery { getFirestoreUserAsFlowUseCase.invoke(DEFAULT_RECEIVER_UID) } returns flowOf(
                provideFirestoreUserEntity().copy(online = false)
            )
            viewModel.onChatTextChanged(DEFAULT_MESSAGE)
            
            viewModel.viewState.observeForTesting(this) {
                assertThat(it.value).isEqualTo(
                    provideChatDetailsViewState().copy(
                        onlineDotResources = R.drawable.gray_circle,
                        sendImageResources = R.drawable.send_colored
                    )
                )
            }
        }
    
    @Test
    fun `nominal case - send message success`() = testCoroutineRule.runTest {
        coEvery { getFirestoreUserAsFlowUseCase.invoke(DEFAULT_RECEIVER_UID) } returns flowOf(
            provideFirestoreUserEntity().copy(online = false)
        )
        viewModel.onChatTextChanged(DEFAULT_MESSAGE)
        viewModel.onSendButtonClicked()
        
        viewModel.viewState.observeForTesting(this) {
            assertThat(it.value).isEqualTo(
                provideChatDetailsViewState().copy(
                    onlineDotResources = R.drawable.gray_circle,
                    sendImageResources = R.drawable.send_colored
                )
            )
        }
        
        coVerify {
            sendMessageUseCase.invoke(DEFAULT_RECEIVER_UID, DEFAULT_MESSAGE)
        }
        
        confirmVerified(sendMessageUseCase)
    }
    
    private fun provideChatDetailsViewState() = ChatDetailsViewState(
        receiverName = DEFAULT_DISPLAY_NAME,
        receiverPicture = DEFAULT_PICTURE,
        onlineDotResources = R.drawable.green_circle,
        sendImageResources = R.drawable.send_uncolored
    )
    
    private fun provideFirestoreUserEntity() = FirestoreUserEntity(
        picture = DEFAULT_PICTURE,
        displayName = DEFAULT_DISPLAY_NAME,
        mailAddress = DEFAULT_MAIL,
        uid = DEFAULT_RECEIVER_UID,
        currentlyEating = DEFAULT_CURRENTLY_EATING,
        eatingPlaceId = DEFAULT_EATING_PLACE_ID,
        online = DEFAULT_IS_ONLINE
    )
}