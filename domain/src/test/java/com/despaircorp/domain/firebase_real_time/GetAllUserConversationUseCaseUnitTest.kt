package com.despaircorp.domain.firebase_real_time

import app.cash.turbine.test
import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firebase_real_time.model.ChatEntity
import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetAllUserConversationUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    
    private val firebaseRealTimeDomainRepository: FirebaseRealTimeDomainRepository = mockk()
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository = mockk()
    
    private val useCase = GetAllUserConversationUseCase(
        firebaseRealTimeDomainRepository = firebaseRealTimeDomainRepository,
        firebaseAuthDomainRepository = firebaseAuthDomainRepository
    )
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_RECEIVER_UID = "DEFAULT_RECEIVER_UID"
        
        private const val DEFAULT_CHAT_TIMESTAMP = 1_000L
        private const val DEFAULT_CHAT_VALUE = "DEFAULT_CHAT_VALUE"
        private const val DEFAULT_CHAT_SNAPSHOT_KEY = "DEFAULT_CHAT_SNAPSHOT_KEY"
    }
    
    @Before
    fun setup() {
        coEvery { firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid } returns DEFAULT_UID
        coEvery { firebaseRealTimeDomainRepository.getChatEntity(DEFAULT_UID) } returns provideChatsEntities()
    }
    
    @Test
    fun `nominal case - get chat entities success`() = testCoroutineRule.runTest {
        useCase.invoke().test {
            awaitComplete()
            
            coVerify {
                firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid
                firebaseRealTimeDomainRepository.getChatEntity(DEFAULT_UID)
            }
            confirmVerified(
                firebaseAuthDomainRepository,
                firebaseRealTimeDomainRepository
            )
        }
    }
    
    private fun provideChatsEntities(): Flow<List<ChatEntity>> = flow {
        ChatEntity(
            chatId = DEFAULT_CHAT_SNAPSHOT_KEY,
            value = DEFAULT_CHAT_VALUE,
            timestamp = DEFAULT_CHAT_TIMESTAMP,
            senderUid = DEFAULT_UID,
            receiverUid = DEFAULT_RECEIVER_UID
        )
    }
}