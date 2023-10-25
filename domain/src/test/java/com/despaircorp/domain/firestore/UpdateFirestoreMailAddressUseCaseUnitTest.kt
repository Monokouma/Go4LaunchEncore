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

class UpdateFirestoreMailAddressUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_MAIL = "DEFAULT_MAIL"
    }
    
    private val firestoreDomainRepository: FirestoreDomainRepository = mockk()
    
    private val useCase = UpdateFirestoreMailAddressUseCase(
        firestoreDomainRepository = firestoreDomainRepository
    )
    
    @Before
    fun setup() {
        coEvery {
            firestoreDomainRepository.updateMailAddress(
                DEFAULT_UID,
                DEFAULT_MAIL
            )
        } returns true
    }
    
    @Test
    fun `nominal case - success`() = testCoroutineRule.runTest {
        val result = useCase.invoke(DEFAULT_UID, DEFAULT_MAIL)
        
        assertThat(result).isTrue()
        
        coVerify { firestoreDomainRepository.updateMailAddress(DEFAULT_UID, DEFAULT_MAIL) }
        
        confirmVerified(firestoreDomainRepository)
    }
    
    @Test
    fun `nominal case - failure`() = testCoroutineRule.runTest {
        coEvery {
            firestoreDomainRepository.updateMailAddress(
                DEFAULT_UID,
                DEFAULT_MAIL
            )
        } returns false
        
        val result = useCase.invoke(DEFAULT_UID, DEFAULT_MAIL)
        
        assertThat(result).isFalse()
        
        coVerify { firestoreDomainRepository.updateMailAddress(DEFAULT_UID, DEFAULT_MAIL) }
        
        confirmVerified(firestoreDomainRepository)
    }
}