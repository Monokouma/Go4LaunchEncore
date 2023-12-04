package com.despaircorp.domain.firebase_real_time

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CreateConversationUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val firebaseRealTimeDomainRepository: FirebaseRealTimeDomainRepository = mockk()
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository = mockk()
    private val getOrderedUidsUseCase: GetOrderedUidsUseCase = mockk()
    
    private val useCase = CreateConversationUseCase(
        firebaseRealTimeDomainRepository = firebaseRealTimeDomainRepository,
        firebaseAuthDomainRepository = firebaseAuthDomainRepository,
        getOrderedUidsUseCase = getOrderedUidsUseCase
    )
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_RECEIVER_UID = "DEFAULT_RECEIVER_UID"
    }
    
    @Before
    fun setup() {
        coEvery { firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid } returns DEFAULT_UID
        coEvery {
            firebaseRealTimeDomainRepository.createConversation(
                DEFAULT_RECEIVER_UID,
                DEFAULT_UID
            )
        } returns flowOf(true)
        coEvery {
            getOrderedUidsUseCase.invoke(DEFAULT_RECEIVER_UID, DEFAULT_UID)
        } returns Pair(
            DEFAULT_RECEIVER_UID, DEFAULT_UID
        )
    }
    
    @Test
    fun `nominal case - create conversation success`() = testCoroutineRule.runTest {
        useCase.invoke(DEFAULT_RECEIVER_UID).test {
            val result = awaitItem()
            awaitComplete()
            
            assertThat(result).isTrue()
            
            coVerify {
                firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid
                firebaseRealTimeDomainRepository.createConversation(
                    DEFAULT_RECEIVER_UID,
                    DEFAULT_UID
                )
            }
            
            confirmVerified(
                firebaseAuthDomainRepository,
                firebaseRealTimeDomainRepository
            )
        }
    }
    
    @Test
    fun `nominal case - create conversation failure`() = testCoroutineRule.runTest {
        coEvery {
            firebaseRealTimeDomainRepository.createConversation(
                DEFAULT_RECEIVER_UID,
                DEFAULT_UID
            )
        } returns flowOf(false)
        
        
        useCase.invoke(DEFAULT_RECEIVER_UID).test {
            val result = awaitItem()
            awaitComplete()
            
            assertThat(result).isFalse()
            
            coVerify {
                firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid
                firebaseRealTimeDomainRepository.createConversation(
                    DEFAULT_RECEIVER_UID,
                    DEFAULT_UID
                )
            }
            
            confirmVerified(
                firebaseAuthDomainRepository,
                firebaseRealTimeDomainRepository
            )
        }
    }
}