package com.despaircorp.domain.firestore

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.despaircorp.domain.firestore.model.FirestoreUserEntity
import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetCurrentEatingRestaurantForAuthenticatedUserUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val firestoreDomainRepository: FirestoreDomainRepository = mockk()
    
    private val useCase = GetCurrentEatingRestaurantForAuthenticatedUserUseCase(
        firestoreDomainRepository
    )
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_MAIL = "DEFAULT_MAIL"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
        private const val DEFAULT_PICTURE = "DEFAULT_PICTURE"
        private const val DEFAULT_IS_ONLINE = true
        private const val DEFAULT_CURRENTLY_EATING = true
        private const val DEFAULT_EATING_PLACE_ID = "DEFAULT_EATING_PLACE_ID"
        private const val DEFAULT_PLACE_ID = "DEFAULT_PLACE_ID"
        
    }
    
    @Before
    fun setup() {
        coEvery { firestoreDomainRepository.getUser(DEFAULT_UID) } returns provideFirestoreUserEntity()
    }
    
    @Test
    fun `nominal case - user eating somewhere`() = testCoroutineRule.runTest {
        val result = useCase.invoke(DEFAULT_UID, DEFAULT_PLACE_ID)
        
        assertThat(result).isTrue()
        
        coVerify {
            firestoreDomainRepository.getUser(DEFAULT_UID)
        }
        
        confirmVerified(firestoreDomainRepository)
    }
    
    @Test
    fun `edge case - use not eating`() = testCoroutineRule.runTest {
        coEvery { firestoreDomainRepository.getUser(DEFAULT_UID) } returns provideFirestoreUserEntity().copy(
            currentlyEating = false,
            eatingPlaceId = null
        )
        
        val result = useCase.invoke(DEFAULT_UID, DEFAULT_PLACE_ID)
        
        assertThat(result).isFalse()
        
        coVerify {
            firestoreDomainRepository.getUser(DEFAULT_UID)
        }
        
        confirmVerified(firestoreDomainRepository)
    }
    
    private fun provideFirestoreUserEntity() = FirestoreUserEntity(
        picture = DEFAULT_PICTURE,
        displayName = DEFAULT_DISPLAY_NAME,
        mailAddress = DEFAULT_MAIL,
        uid = DEFAULT_UID,
        currentlyEating = DEFAULT_CURRENTLY_EATING,
        eatingPlaceId = DEFAULT_PLACE_ID,
        online = DEFAULT_IS_ONLINE
    )
}