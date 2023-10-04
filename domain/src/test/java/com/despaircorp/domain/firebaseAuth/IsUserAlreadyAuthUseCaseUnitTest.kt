package com.despaircorp.domain.firebaseAuth

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class IsUserAlreadyAuthUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository = mockk()
    
    private val useCase = IsUserAlreadyAuthUseCase(
        firebaseAuthDomainRepository
    )
    
    @Before
    fun setup() {
        coEvery { firebaseAuthDomainRepository.isUserAlreadyAuth() } returns true
        
    }
    
    @Test
    fun `nominal case - success`() = testCoroutineRule.runTest {
        val result = useCase.invoke()
        
        assertThat(result).isTrue()
        
        coVerify {
            firebaseAuthDomainRepository.isUserAlreadyAuth()
        }
        
        confirmVerified(firebaseAuthDomainRepository)
    }
    
    @Test
    fun `nominal case - failure`() = testCoroutineRule.runTest {
        coEvery { firebaseAuthDomainRepository.isUserAlreadyAuth() } returns false
        
        val result = useCase.invoke()
        
        assertThat(result).isFalse()
        
        coVerify {
            firebaseAuthDomainRepository.isUserAlreadyAuth()
        }
        
        confirmVerified(firebaseAuthDomainRepository)
    }
}