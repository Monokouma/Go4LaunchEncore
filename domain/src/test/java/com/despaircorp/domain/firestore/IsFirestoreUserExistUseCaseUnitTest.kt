package com.despaircorp.domain.firestore

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

class IsFirestoreUserExistUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        
        
    }
    
    private val firestoreDomainRepository: FirestoreDomainRepository = mockk()
    
    private val useCase = IsFirestoreUserExistUseCase(
        firestoreDomainRepository
    )
    
    @Before
    fun setup() {
        coEvery { firestoreDomainRepository.isUserExist(DEFAULT_UID) } returns true
    }
    
    @Test
    fun `nominal case - success`() = testCoroutineRule.runTest {
        val result = useCase.invoke(DEFAULT_UID)
        
        assertThat(result).isTrue()
        
        coVerify {
            firestoreDomainRepository.isUserExist(DEFAULT_UID)
        }
        
        confirmVerified(firestoreDomainRepository)
    }
    
    @Test
    fun `nominal case - failure`() = testCoroutineRule.runTest {
        coEvery { firestoreDomainRepository.isUserExist(DEFAULT_UID) } returns false
        
        val result = useCase.invoke(DEFAULT_UID)
        
        assertThat(result).isFalse()
        
        coVerify {
            firestoreDomainRepository.isUserExist(DEFAULT_UID)
        }
        
        confirmVerified(firestoreDomainRepository)
    }
}