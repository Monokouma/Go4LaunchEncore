package com.despaircorp.domain.firestore

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.firestore.model.FirestoreUserEntity
import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetFirestoreUserUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_MAIL = "DEFAULT_MAIL"
        private const val DEFAULT_PASSWORD = "DEFAULT_PASSWORD"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
        private const val DEFAULT_PICTURE = "DEFAULT_PICTURE"
        private const val DEFAULT_TOKEN = "DEFAULT_TOKEN"
        private const val DEFAULT_CURRENTLY_EATING = false
        private val DEFAULT_EATING_PLACE_IDE = null
    }
    
    private val firestoreDomainRepository: FirestoreDomainRepository = mockk()
    
    private val useCase = GetFirestoreUserUseCase(
        firestoreDomainRepository
    )
    
    @Before
    fun setup() {
        coEvery { firestoreDomainRepository.getUser(DEFAULT_UID) } returns provideFirestoreUserEntity()
    }
    
    @Test
    fun `nominal case - success`() = testCoroutineRule.runTest {
        val result = useCase.invoke(DEFAULT_UID)
        
        assertThat(result).isEqualTo(provideFirestoreUserEntity())
        
        coVerify {
            firestoreDomainRepository.getUser(DEFAULT_UID)
        }
        
        confirmVerified(firestoreDomainRepository)
    }
    
    //Region Out
    private fun provideFirestoreUserEntity() = FirestoreUserEntity(
        picture = DEFAULT_PICTURE,
        displayName = DEFAULT_DISPLAY_NAME,
        mailAddress = DEFAULT_MAIL,
        uid = DEFAULT_UID,
        currentlyEating = DEFAULT_CURRENTLY_EATING,
        eatingPlaceId = DEFAULT_EATING_PLACE_IDE
    )
    
    //End region out
}