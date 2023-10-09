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

class DisconnectUserUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository = mockk()
    
    private val useCase = DisconnectUserUseCase(
        firebaseAuthDomainRepository = firebaseAuthDomainRepository
    )
    
    @Before
    fun setup() {
        coEvery { firebaseAuthDomainRepository.disconnectUser() } returns true
    }
    
    @Test
    fun `nominal case - disconnect user success`() = testCoroutineRule.runTest {
        val result = useCase.invoke()
        
        assertThat(result).isTrue()
        
        coVerify {
            firebaseAuthDomainRepository.disconnectUser()
        }
        
        confirmVerified(firebaseAuthDomainRepository)
    }
    
    @Test
    fun `nominal case - disconnect user failure`() = testCoroutineRule.runTest {
        coEvery { firebaseAuthDomainRepository.disconnectUser() } returns false
        
        val result = useCase.invoke()
        
        assertThat(result).isFalse()
        
        coVerify {
            firebaseAuthDomainRepository.disconnectUser()
        }
        
        confirmVerified(firebaseAuthDomainRepository)
    }
}