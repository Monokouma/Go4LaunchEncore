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

class IsUserWithCredentialsSignedInUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    companion object {
        private const val DEFAULT_EMAIL = "DEFAULT_EMAIL"
        private const val DEFAULT_PASSWORD = "DEFAULT_PASSWORD"
    }
    
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository = mockk()
    
    private val useCase = IsUserWithCredentialsSignedInUseCase(
        firebaseAuthDomainRepository
    )
    
    @Before
    fun setup() {
        coEvery {
            firebaseAuthDomainRepository.isUserWithCredentialsExist(
                DEFAULT_EMAIL,
                DEFAULT_PASSWORD
            )
        } returns true
    }
    
    @Test
    fun `nominal case - success`() = testCoroutineRule.runTest {
        val result = useCase.invoke(DEFAULT_EMAIL, DEFAULT_PASSWORD)
        
        assertThat(result).isTrue()
        
        coVerify {
            firebaseAuthDomainRepository.isUserWithCredentialsExist(
                DEFAULT_EMAIL,
                DEFAULT_PASSWORD
            )
        }
        
        confirmVerified(firebaseAuthDomainRepository)
    }
    
    @Test
    fun `nominal case - failure`() = testCoroutineRule.runTest {
        coEvery {
            firebaseAuthDomainRepository.isUserWithCredentialsExist(
                DEFAULT_EMAIL,
                DEFAULT_PASSWORD
            )
        } returns false
        
        val result = useCase.invoke(DEFAULT_EMAIL, DEFAULT_PASSWORD)
        
        assertThat(result).isFalse()
        
        coVerify {
            firebaseAuthDomainRepository.isUserWithCredentialsExist(
                DEFAULT_EMAIL,
                DEFAULT_PASSWORD
            )
        }
        
        confirmVerified(firebaseAuthDomainRepository)
    }
}