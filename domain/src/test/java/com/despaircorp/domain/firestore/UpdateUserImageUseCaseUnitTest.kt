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

class UpdateUserImageUseCaseUnitTest {
    
    
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_PICTURE = "DEFAULT_PICTURE"
        
    }
    
    private val firestoreDomainRepository: FirestoreDomainRepository = mockk()
    
    private val useCase = UpdateUserImageUseCase(
        firestoreDomainRepository = firestoreDomainRepository
    )
    
    @Before
    fun setup() {
        coEvery {
            firestoreDomainRepository.updateUserImage(
                DEFAULT_UID,
                DEFAULT_PICTURE
            )
        } returns true
    }
    
    @Test
    fun `nominal case - success`() = testCoroutineRule.runTest {
        val result = useCase.invoke(DEFAULT_UID, DEFAULT_PICTURE)
        
        assertThat(result).isTrue()
        
        coVerify {
            firestoreDomainRepository.updateUserImage(DEFAULT_UID, DEFAULT_PICTURE)
        }
        confirmVerified(firestoreDomainRepository)
    }
    
    @Test
    fun `nominal case - error`() = testCoroutineRule.runTest {
        coEvery {
            firestoreDomainRepository.updateUserImage(
                DEFAULT_UID,
                DEFAULT_PICTURE
            )
        } returns false
        
        val result = useCase.invoke(DEFAULT_UID, DEFAULT_PICTURE)
        
        assertThat(result).isFalse()
        
        coVerify {
            firestoreDomainRepository.updateUserImage(DEFAULT_UID, DEFAULT_PICTURE)
        }
        confirmVerified(firestoreDomainRepository)
    }
}