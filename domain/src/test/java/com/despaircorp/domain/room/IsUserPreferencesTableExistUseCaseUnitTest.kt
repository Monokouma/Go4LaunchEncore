package com.despaircorp.domain.room

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

class IsUserPreferencesTableExistUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val roomDomainRepository: RoomDomainRepository = mockk()
    
    private val useCase = IsUserPreferencesTableExistUseCase(
        roomDomainRepository = roomDomainRepository
    )
    
    @Before
    fun setup() {
        coEvery { roomDomainRepository.exist() } returns true
    }
    
    @Test
    fun `nominal case - exist`() = testCoroutineRule.runTest {
        val result = useCase.invoke()
        
        assertThat(result).isTrue()
        
        coVerify {
            roomDomainRepository.exist()
        }
        
        confirmVerified(roomDomainRepository)
    }
    
    @Test
    fun `nominal case - not exist`() = testCoroutineRule.runTest {
        coEvery { roomDomainRepository.exist() } returns false
        
        val result = useCase.invoke()
        
        assertThat(result).isFalse()
        
        coVerify {
            roomDomainRepository.exist()
        }
        
        confirmVerified(roomDomainRepository)
    }
}