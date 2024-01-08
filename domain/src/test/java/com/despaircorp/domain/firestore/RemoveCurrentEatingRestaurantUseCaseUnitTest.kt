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

class RemoveCurrentEatingRestaurantUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val firestoreDomainRepository: FirestoreDomainRepository = mockk()
    
    private val useCase = RemoveCurrentEatingRestaurantUseCase(firestoreDomainRepository)
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
    }
    
    @Before
    fun setup() {
        coEvery { firestoreDomainRepository.removeCurrentEatingRestaurant(DEFAULT_UID) } returns true
    }
    
    @Test
    fun `nominal case - success`() = testCoroutineRule.runTest {
        val result = useCase.invoke(DEFAULT_UID)
        
        assertThat(result).isTrue()
        
        coVerify {
            firestoreDomainRepository.removeCurrentEatingRestaurant(DEFAULT_UID)
        }
        
        confirmVerified(firestoreDomainRepository)
    }
}