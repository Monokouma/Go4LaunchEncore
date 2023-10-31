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

class UpdateAuthMailAddressUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository = mockk()
    
    
    private val useCase = UpdateAuthMailAddressUseCase(
        firebaseAuthDomainRepository
    )
    
    companion object {
        private const val DEFAULT_MAIL = "DEFAULT_MAIL"
    }
    
    @Before
    fun setup() {
        coEvery { firebaseAuthDomainRepository.updateMailAddress(DEFAULT_MAIL) } returns true
    }
    
    @Test
    fun `nominal case - success`() = testCoroutineRule.runTest {
        val result = useCase.invoke(DEFAULT_MAIL)
        
        assertThat(result).isTrue()
        
        coVerify {
            firebaseAuthDomainRepository.updateMailAddress(DEFAULT_MAIL)
        }
        
        confirmVerified(firebaseAuthDomainRepository)
    }
    
    @Test
    fun `nominal case - failure`() = testCoroutineRule.runTest {
        coEvery { firebaseAuthDomainRepository.updateMailAddress(DEFAULT_MAIL) } returns false
        
        val result = useCase.invoke(DEFAULT_MAIL)
        
        assertThat(result).isFalse()
        
        coVerify {
            firebaseAuthDomainRepository.updateMailAddress(DEFAULT_MAIL)
        }
        
        confirmVerified(firebaseAuthDomainRepository)
    }
}