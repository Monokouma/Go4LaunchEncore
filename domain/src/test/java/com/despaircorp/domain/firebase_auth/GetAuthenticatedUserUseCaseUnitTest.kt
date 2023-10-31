package com.despaircorp.domain.firebase_auth

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.firebase_auth.model.AuthenticateUserEntity
import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetAuthenticatedUserUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository = mockk()
    
    private val useCase = GetAuthenticatedUserUseCase(
        firebaseAuthDomainRepository
    )
    
    companion object {
        private const val DEFAULT_MAIL = "DEFAULT_MAIL"
        private const val DEFAULT_PASSWORD = "DEFAULT_PASSWORD"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_PICTURE = "DEFAULT_PICTURE"
        private const val DEFAULT_TOKEN = "DEFAULT_TOKEN"
        private const val DEFAULT_CURRENTLY_EATING = false
        private val DEFAULT_EATING_PLACE_IDE = null
    }
    
    @Before
    fun setup() {
        coEvery { firebaseAuthDomainRepository.getCurrentAuthenticatedUser() } returns provideAuthenticatedUserEntity()
    }
    
    @Test
    fun `nominal case - get user success`() = testCoroutineRule.runTest {
        val result = useCase.invoke()
        
        assertThat(result).isEqualTo(
            provideAuthenticatedUserEntity()
        )
        coVerify {
            firebaseAuthDomainRepository.getCurrentAuthenticatedUser()
        }
        
        confirmVerified(firebaseAuthDomainRepository)
    }
    
    //Region Out
    private fun provideAuthenticatedUserEntity() = AuthenticateUserEntity(
        picture = DEFAULT_PICTURE,
        displayName = DEFAULT_DISPLAY_NAME,
        mailAddress = DEFAULT_MAIL,
        uid = DEFAULT_UID,
        currentlyEating = DEFAULT_CURRENTLY_EATING,
        eatingPlaceId = DEFAULT_EATING_PLACE_IDE
    )
}