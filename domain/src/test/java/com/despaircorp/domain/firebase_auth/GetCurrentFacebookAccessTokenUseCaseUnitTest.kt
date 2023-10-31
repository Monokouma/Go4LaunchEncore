package com.despaircorp.domain.firebase_auth

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetCurrentFacebookAccessTokenUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository = mockk()
    
    private val useCase = GetCurrentFacebookAccessTokenUseCase(
        firebaseAuthDomainRepository = firebaseAuthDomainRepository
    )
    
    companion object {
        private const val DEFAULT_TOKEN = "DEFAULT_TOKEN"
    }
    
    @Before
    fun setup() {
        coEvery { firebaseAuthDomainRepository.getCurrentFacebookAccessToken() } returns DEFAULT_TOKEN
    }
    
    @Test
    fun `nominal case - get current token success`() = testCoroutineRule.runTest {
        val result = useCase.invoke()
        
        assertThat(result).isEqualTo(DEFAULT_TOKEN)
        
        coVerify {
            firebaseAuthDomainRepository.getCurrentFacebookAccessToken()
        }
        
        confirmVerified(firebaseAuthDomainRepository)
    }
    
    @Test
    fun `nominal case - get current token null`() = testCoroutineRule.runTest {
        coEvery { firebaseAuthDomainRepository.getCurrentFacebookAccessToken() } returns null
        
        val result = useCase.invoke()
        
        assertThat(result).isNull()
        
        coVerify {
            firebaseAuthDomainRepository.getCurrentFacebookAccessToken()
        }
        
        confirmVerified(firebaseAuthDomainRepository)
    }
}