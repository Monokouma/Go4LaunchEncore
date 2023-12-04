package com.despaircorp.ui.main.chat.menu

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.firebase_auth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firebase_real_time.CreateConversationUseCase
import com.despaircorp.domain.firebase_real_time.GetAllUserConversationUseCase
import com.despaircorp.domain.firebase_real_time.model.ChatEntity
import com.despaircorp.domain.firestore.GetAllCoworkersForChatUseCase
import com.despaircorp.domain.firestore.GetFirestoreUserUseCase
import com.despaircorp.domain.firestore.model.CoworkersChatEntity
import com.despaircorp.ui.R
import com.despaircorp.ui.main.chat.menu.messages.ChatMenuMessagesViewStateItems
import com.despaircorp.ui.main.chat.menu.online_users.ChatMenuOnlineUserViewStateItems
import com.despaircorp.ui.utils.NativeText
import com.despaircorp.ui.utils.TestCoroutineRule
import com.despaircorp.ui.utils.observeForTesting
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChatMenuViewModelUnitTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val getAllCoworkersForChatUseCase: GetAllCoworkersForChatUseCase = mockk()
    private val createConversationUseCase: CreateConversationUseCase = mockk()
    private val getAllUserConversationUseCase: GetAllUserConversationUseCase = mockk()
    private val getFirestoreUserUseCase: GetFirestoreUserUseCase = mockk()
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase = mockk()
    private val application: Application = mockk()
    private lateinit var viewModel: ChatMenuViewModel
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
        private const val DEFAULT_PICTURE = "DEFAULT_PICTURE"
        private const val DEFAULT_IS_ONLINE = false
        private const val DEFAULT_SENDER_UID = "DEFAULT_SENDER_UID"
        private const val DEFAULT_RECEIVER_UID = "DEFAULT_RECEIVER_UID"
        
        
        private const val DEFAULT_CHAT_TIMESTAMP = 1_000L
        private const val DEFAULT_CHAT_VALUE = "DEFAULT_CHAT_VALUE"
        private const val DEFAULT_CHAT_SNAPSHOT_KEY = "DEFAULT_CHAT_SNAPSHOT_KEY"
    }
    
    @Before
    fun setup() {
        coEvery { getAllCoworkersForChatUseCase.invoke() } returns flowOf(
            provideCoworkerChatEntities()
        )
        coEvery { getAllUserConversationUseCase.invoke() } returns flowOf(provideChatsEntity())
        coEvery { getAuthenticatedUserUseCase.invoke().uid } returns DEFAULT_UID
        coEvery { getFirestoreUserUseCase.invoke(DEFAULT_RECEIVER_UID).displayName } returns DEFAULT_DISPLAY_NAME
        coEvery { getFirestoreUserUseCase.invoke(DEFAULT_RECEIVER_UID).picture } returns DEFAULT_PICTURE
        coEvery { createConversationUseCase.invoke(DEFAULT_RECEIVER_UID) } returns flowOf(true)
        every { application.getString(R.string.you) } returns "You"
        
        viewModel = ChatMenuViewModel(
            getAllCoworkersForChatUseCase = getAllCoworkersForChatUseCase,
            createConversationUseCase = createConversationUseCase,
            getAllUserConversationUseCase = getAllUserConversationUseCase,
            getFirestoreUserUseCase = getFirestoreUserUseCase,
            getAuthenticatedUserUseCase = getAuthenticatedUserUseCase,
            application = application
        )
    }
    
    @Test
    fun `nominal case - get list of user and conversation`() = testCoroutineRule.runTest {
        viewModel.viewState.observeForTesting(this) {
            assertThat(it.value).isEqualTo(
                provideChatMenuViewState()
            )
        }
    }
    
    @Test
    fun `nominal case - get list of user and conversation with online user and current user last message`() =
        testCoroutineRule.runTest {
            coEvery { getAllCoworkersForChatUseCase.invoke() } returns flowOf(
                provideOnlineCoworkerChatEntities()
            )
            coEvery { getAuthenticatedUserUseCase.invoke().uid } returns DEFAULT_SENDER_UID
            
            viewModel.viewState.observeForTesting(this) {
                assertThat(it.value).isEqualTo(
                    provideChatMenuViewState().copy(
                        chatMenuOnlineUserViewStateItems = provideChatMenuOnlineUserViewStateItemsWithOnlineUser(),
                        chatMessagesViewStateItems = provideChatMessagesViewStateItemsWithYouAsMessage()
                    )
                )
            }
        }
    
    
    @Test
    fun `error case - create conversation error`() = testCoroutineRule.runTest {
        coEvery { createConversationUseCase.invoke(DEFAULT_RECEIVER_UID) } returns flowOf(false)
        
        viewModel.createConversation(DEFAULT_RECEIVER_UID)
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(
                ChatMenuAction.Error(
                    NativeText.Resource(R.string.error_occurred)
                )
            )
        }
    }
    
    
    private fun provideChatMenuViewState() = ChatMenuViewState(
        chatMessagesViewStateItems = provideChatMessagesViewStateItems(),
        chatMenuOnlineUserViewStateItems = provideChatMenuOnlineUserViewStateItems()
    )
    
    
    private fun provideChatMenuOnlineUserViewStateItems() = List(3) {
        ChatMenuOnlineUserViewStateItems(
            uid = DEFAULT_RECEIVER_UID,
            name = DEFAULT_DISPLAY_NAME,
            dotDrawable = R.drawable.gray_circle,
            userImageAlpha = 0.6f,
            pictureUrl = DEFAULT_PICTURE,
            online = DEFAULT_IS_ONLINE
        )
    }
    
    private fun provideChatMenuOnlineUserViewStateItemsWithOnlineUser() = List(3) {
        ChatMenuOnlineUserViewStateItems(
            uid = DEFAULT_RECEIVER_UID,
            name = DEFAULT_DISPLAY_NAME,
            dotDrawable = R.drawable.green_circle,
            userImageAlpha = 1.0f,
            pictureUrl = DEFAULT_PICTURE,
            online = true
        )
    }
    
    private fun provideChatMessagesViewStateItems() = List(3) {
        ChatMenuMessagesViewStateItems(
            convId = DEFAULT_CHAT_SNAPSHOT_KEY,
            senderId = DEFAULT_SENDER_UID,
            receiverId = DEFAULT_RECEIVER_UID,
            message = "${DEFAULT_DISPLAY_NAME}: ${DEFAULT_CHAT_VALUE}• 01:00",
            receiverImage = DEFAULT_PICTURE,
            receiverName = DEFAULT_DISPLAY_NAME,
            timestamp = DEFAULT_CHAT_TIMESTAMP
        )
    }
    
    private fun provideChatMessagesViewStateItemsWithYouAsMessage() = List(3) {
        ChatMenuMessagesViewStateItems(
            convId = DEFAULT_CHAT_SNAPSHOT_KEY,
            senderId = DEFAULT_SENDER_UID,
            receiverId = DEFAULT_RECEIVER_UID,
            message = "You: ${DEFAULT_CHAT_VALUE}• 01:00",
            receiverImage = DEFAULT_PICTURE,
            receiverName = DEFAULT_DISPLAY_NAME,
            timestamp = DEFAULT_CHAT_TIMESTAMP
        )
    }
    
    private fun provideChatsEntity() = List<ChatEntity>(3) {
        ChatEntity(
            chatId = DEFAULT_CHAT_SNAPSHOT_KEY,
            value = DEFAULT_CHAT_VALUE,
            timestamp = DEFAULT_CHAT_TIMESTAMP,
            senderUid = DEFAULT_SENDER_UID,
            receiverUid = DEFAULT_RECEIVER_UID
        )
    }
    
    private fun provideCoworkerChatEntities() = List(3) {
        CoworkersChatEntity(
            DEFAULT_RECEIVER_UID,
            DEFAULT_PICTURE,
            DEFAULT_DISPLAY_NAME,
            DEFAULT_IS_ONLINE
        )
    }
    
    private fun provideOnlineCoworkerChatEntities() = List(3) {
        CoworkersChatEntity(
            DEFAULT_RECEIVER_UID,
            DEFAULT_PICTURE,
            DEFAULT_DISPLAY_NAME,
            true
        )
    }
}