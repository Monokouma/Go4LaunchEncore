package com.despaircorp.domain.firebase_real_time

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firebase_real_time.model.ChatEntity
import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetAllUserMessagesWithSpecificUserUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val firebaseRealTimeDomainRepository: FirebaseRealTimeDomainRepository = mockk()
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository = mockk()
    
    private val useCase = GetAllUserMessagesWithSpecificUserUseCase(
        firebaseRealTimeDomainRepository = firebaseRealTimeDomainRepository,
        firebaseAuthDomainRepository = firebaseAuthDomainRepository
    )
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_SPECIFIC_UID = "DEFAULT_SPECIFIC_UID"
        private const val DEFAULT_CHAT_TIMESTAMP = 1_000L
        private const val DEFAULT_CHAT_VALUE = "DEFAULT_CHAT_VALUE"
        private const val DEFAULT_CHAT_SNAPSHOT_KEY = "DEFAULT_CHAT_SNAPSHOT_KEY"
    }
    
    @Before
    fun setup() {
        coEvery { firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid } returns DEFAULT_UID
        coEvery {
            firebaseRealTimeDomainRepository.getMessagesBetweenCurrentUserAndSpecificUser(
                DEFAULT_UID, DEFAULT_SPECIFIC_UID
            )
        } returns flowOf(provideChatEntities())
    }
    
    @Test
    fun `nominal case - get list`() = testCoroutineRule.runTest {
        useCase.invoke(DEFAULT_SPECIFIC_UID).test {
            val result = awaitItem()
            awaitComplete()
            
            assertThat(result).isEqualTo(
                provideChatEntities()
            )
            
            coVerify {
                firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid
                firebaseRealTimeDomainRepository.getMessagesBetweenCurrentUserAndSpecificUser(
                    DEFAULT_UID, DEFAULT_SPECIFIC_UID
                )
            }
            
            confirmVerified(
                firebaseRealTimeDomainRepository,
                firebaseAuthDomainRepository
            )
        }
    }
    
    //Region OUT
    private fun provideChatEntities() = List(3) {
        ChatEntity(
            chatId = DEFAULT_CHAT_SNAPSHOT_KEY,
            value = DEFAULT_CHAT_VALUE,
            timestamp = DEFAULT_CHAT_TIMESTAMP,
            senderUid = DEFAULT_UID,
            receiverUid = DEFAULT_SPECIFIC_UID
        )
    }
    
    //End region OUT
}