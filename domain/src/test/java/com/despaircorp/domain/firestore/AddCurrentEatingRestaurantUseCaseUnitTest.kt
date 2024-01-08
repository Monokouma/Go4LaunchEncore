package com.despaircorp.domain.firestore

import assertk.assertThat
import assertk.assertions.isTrue
import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddCurrentEatingRestaurantUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val firestoreDomainRepository: FirestoreDomainRepository = mockk()
    
    private val useCase = AddCurrentEatingRestaurantUseCase(
        firestoreDomainRepository
    )
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_PLACE_ID = "DEFAULT_PLACE_ID"
    }
    
    @Before
    fun setup() {
        coEvery {
            firestoreDomainRepository.addCurrentEatingRestaurant(
                DEFAULT_PLACE_ID,
                DEFAULT_UID
            )
        } returns true
    }
    
    @Test
    fun `nominal case - success`() = testCoroutineRule.runTest {
        val result = useCase.invoke(DEFAULT_PLACE_ID, DEFAULT_UID)
        
        assertThat(result).isTrue()
        
        coVerify {
            firestoreDomainRepository.addCurrentEatingRestaurant(DEFAULT_PLACE_ID, DEFAULT_UID)
        }
        
        confirmVerified(firestoreDomainRepository)
    }
}