package com.despaircorp.domain.firebase_real_time

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firebase_real_time.model.ChatEntity
import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class SendMessageUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    
    private val getOrderedUidsUseCase: GetOrderedUidsUseCase = mockk()
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository = mockk()
    private val firebaseRealTimeDomainRepository: FirebaseRealTimeDomainRepository = mockk()
    
    private val useCase = SendMessageUseCase(
        getOrderedUidsUseCase = getOrderedUidsUseCase,
        firebaseRealTimeDomainRepository = firebaseRealTimeDomainRepository,
        firebaseAuthDomainRepository = firebaseAuthDomainRepository,
        clock = Clock.fixed(
            Instant.ofEpochSecond(1701368499), // 30/11/2023 - 19:21:39
            ZoneOffset.UTC
        )
    )
    
    companion object {
        private const val DEFAULT_SENDER_UID = "DEFAULT_SENDER_UID"
        private const val DEFAULT_RECEIVER_UID = "DEFAULT_RECEIVER_UID"
        private const val DEFAULT_MESSAGE = "DEFAULT_MESSAGE"
        
        private const val DEFAULT_TODAY_AT_MILLIS = 1701364899000L
        private const val DEFAULT_CHAT_ID = "${DEFAULT_SENDER_UID}_$DEFAULT_TODAY_AT_MILLIS"
    }
    
    @Before
    fun setup() {
        
        coEvery { firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid } returns DEFAULT_SENDER_UID
        
        coEvery {
            firebaseRealTimeDomainRepository.insertMessage(
                DEFAULT_RECEIVER_UID,
                DEFAULT_SENDER_UID,
                provideChatEntity()
            )
        } returns true
        
        coEvery {
            getOrderedUidsUseCase.invoke(
                DEFAULT_RECEIVER_UID,
                DEFAULT_SENDER_UID
            )
        } returns Pair(
            DEFAULT_RECEIVER_UID,
            DEFAULT_SENDER_UID
        )
    }
    
    @Test
    fun `nominal case - send message success`() = testCoroutineRule.runTest {
        val result = useCase.invoke(DEFAULT_RECEIVER_UID, DEFAULT_MESSAGE)
        
        assertThat(result).isTrue()
        
        coVerify {
            firebaseRealTimeDomainRepository.insertMessage(
                DEFAULT_RECEIVER_UID,
                DEFAULT_SENDER_UID,
                provideChatEntity()
            )
            firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid
        }
        
        confirmVerified(
            firebaseAuthDomainRepository,
            firebaseRealTimeDomainRepository
        )
    }
    
    @Test
    fun `nominal case - send message failure`() = testCoroutineRule.runTest {
        coEvery {
            firebaseRealTimeDomainRepository.insertMessage(
                DEFAULT_RECEIVER_UID,
                DEFAULT_SENDER_UID,
                provideChatEntity()
            )
        } returns false
        
        val result = useCase.invoke(DEFAULT_RECEIVER_UID, DEFAULT_MESSAGE)
        
        assertThat(result).isFalse()
        
        coVerify {
            firebaseRealTimeDomainRepository.insertMessage(
                DEFAULT_RECEIVER_UID,
                DEFAULT_SENDER_UID,
                provideChatEntity()
            )
            firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid
        }
        
        confirmVerified(
            firebaseAuthDomainRepository,
            firebaseRealTimeDomainRepository
        )
    }
    
    //Region IN
    private fun provideChatEntity() = ChatEntity(
        chatId = DEFAULT_CHAT_ID,
        value = DEFAULT_MESSAGE,
        timestamp = DEFAULT_TODAY_AT_MILLIS,
        senderUid = DEFAULT_SENDER_UID,
        receiverUid = DEFAULT_RECEIVER_UID
    )
    
    //End Region IN
}