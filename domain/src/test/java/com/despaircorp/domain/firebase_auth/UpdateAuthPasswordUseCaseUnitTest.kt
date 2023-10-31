package com.despaircorp.domain.firebase_auth

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

class UpdateAuthPasswordUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository = mockk()
    
    
    private val useCase = UpdateAuthPasswordUseCase(
        firebaseAuthDomainRepository
    )
    
    companion object {
        private const val DEFAULT_PASSWORD = "DEFAULT_PASSWORD"
    }
    
    @Before
    fun setup() {
        coEvery { firebaseAuthDomainRepository.updatePassword(DEFAULT_PASSWORD) } returns true
    }
    
    @Test
    fun `nominal case - success`() = testCoroutineRule.runTest {
        val result = useCase.invoke(DEFAULT_PASSWORD)
        
        assertThat(result).isTrue()
        
        coVerify {
            firebaseAuthDomainRepository.updatePassword(DEFAULT_PASSWORD)
        }
        confirmVerified(firebaseAuthDomainRepository)
    }
    
    @Test
    fun `nominal case - failure`() = testCoroutineRule.runTest {
        coEvery { firebaseAuthDomainRepository.updatePassword(DEFAULT_PASSWORD) } returns false
        
        val result = useCase.invoke(DEFAULT_PASSWORD)
        
        assertThat(result).isFalse()
        
        coVerify {
            firebaseAuthDomainRepository.updatePassword(DEFAULT_PASSWORD)
        }
        confirmVerified(firebaseAuthDomainRepository)
    }
}