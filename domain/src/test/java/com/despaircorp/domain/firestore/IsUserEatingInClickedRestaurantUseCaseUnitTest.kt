package com.despaircorp.domain.firestore

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isTrue
import com.despaircorp.domain.firebase_auth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class IsUserEatingInClickedRestaurantUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val firestoreDomainRepository: FirestoreDomainRepository = mockk()
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase = mockk()
    
    private val useCase = IsUserEatingInClickedRestaurantUseCase(
        firestoreDomainRepository = firestoreDomainRepository,
        getAuthenticatedUserUseCase = getAuthenticatedUserUseCase
    )
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_PLACE_ID = "DEFAULT_PLACE_ID"
        
    }
    
    @Before
    fun setup() {
        coEvery {
            firestoreDomainRepository.getCurrentEatingRestaurantForAuthenticatedUser(
                DEFAULT_UID
            )
        } returns flowOf(DEFAULT_PLACE_ID)
        coEvery { getAuthenticatedUserUseCase.invoke().uid } returns DEFAULT_UID
    }
    
    @Test
    fun `nominal case - user eating`() = testCoroutineRule.runTest {
        useCase.invoke(DEFAULT_PLACE_ID).test {
            val result = awaitItem()
            awaitComplete()
            
            assertThat(result).isTrue()
            
            coVerify {
                firestoreDomainRepository.getCurrentEatingRestaurantForAuthenticatedUser(DEFAULT_UID)
                getAuthenticatedUserUseCase.invoke().uid
            }
            
            confirmVerified(
                firestoreDomainRepository,
                getAuthenticatedUserUseCase
            )
        }
    }
}