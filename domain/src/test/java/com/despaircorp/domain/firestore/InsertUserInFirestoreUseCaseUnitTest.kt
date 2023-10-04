package com.despaircorp.domain.firestore

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.despaircorp.domain.firebaseAuth.model.AuthenticateUserEntity
import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class InsertUserInFirestoreUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_MAIL = "DEFAULT_MAIL"
        private const val DEFAULT_PASSWORD = "DEFAULT_PASSWORD"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
        private const val DEFAULT_PICTURE = "DEFAULT_PICTURE"
        private const val DEFAULT_TOKEN = "DEFAULT_TOKEN"
        
    }
    
    private val firestoreDomainRepository: FirestoreDomainRepository = mockk()
    
    private val useCase = InsertUserInFirestoreUseCase(
        firestoreDomainRepository
    )
    
    @Before
    fun setup() {
        coEvery { firestoreDomainRepository.insertUser(provideAuthenticatedUserEntity()) } returns true
    }
    
    @Test
    fun `nominal case - success`() = testCoroutineRule.runTest {
        val result = useCase.invoke(provideAuthenticatedUserEntity())
        
        assertThat(result).isTrue()
        
        coVerify {
            firestoreDomainRepository.insertUser(provideAuthenticatedUserEntity())
        }
        
        confirmVerified(firestoreDomainRepository)
    }
    
    @Test
    fun `nominal case - failure`() = testCoroutineRule.runTest {
        coEvery { firestoreDomainRepository.insertUser(provideAuthenticatedUserEntity()) } returns false
        
        val result = useCase.invoke(provideAuthenticatedUserEntity())
        
        assertThat(result).isFalse()
        
        coVerify {
            firestoreDomainRepository.insertUser(provideAuthenticatedUserEntity())
        }
        
        confirmVerified(firestoreDomainRepository)
    }
    
    private fun provideAuthenticatedUserEntity() = AuthenticateUserEntity(
        picture = DEFAULT_PICTURE,
        displayName = DEFAULT_DISPLAY_NAME,
        mailAddress = DEFAULT_MAIL,
        uid = DEFAULT_UID
    )
}