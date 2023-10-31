package com.despaircorp.domain.firebase_auth

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.despaircorp.domain.utils.TestCoroutineRule
import com.facebook.AccessToken
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SignInTokenUserUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository = mockk()
    
    private val accessToken: AccessToken = mockk()
    
    private val useCase = SignInTokenUserUseCase(
        firebaseAuthDomainRepository
    )
    
    @Before
    fun setup() {
        coEvery { firebaseAuthDomainRepository.signInTokenUser(accessToken) } returns true
    }
    
    @Test
    fun `nominal case - success`() = testCoroutineRule.runTest {
        val result = useCase.invoke(accessToken)
        
        assertThat(result).isTrue()
        
        coVerify {
            firebaseAuthDomainRepository.signInTokenUser(accessToken)
        }
        
        confirmVerified(firebaseAuthDomainRepository)
    }
    
    @Test
    fun `nominal case - failure`() = testCoroutineRule.runTest {
        coEvery { firebaseAuthDomainRepository.signInTokenUser(accessToken) } returns false
        
        val result = useCase.invoke(accessToken)
        
        assertThat(result).isFalse()
        
        coVerify {
            firebaseAuthDomainRepository.signInTokenUser(accessToken)
        }
        
        confirmVerified(firebaseAuthDomainRepository)
    }
}