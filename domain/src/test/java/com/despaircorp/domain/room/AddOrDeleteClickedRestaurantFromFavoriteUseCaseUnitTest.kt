package com.despaircorp.domain.room

import assertk.assertThat
import assertk.assertions.isTrue
import com.despaircorp.domain.room.model.ClickedRestaurantsEntity
import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddOrDeleteClickedRestaurantFromFavoriteUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val roomDomainRepository: RoomDomainRepository = mockk()
    
    private val useCase = AddOrDeleteClickedRestaurantFromFavoriteUseCase(roomDomainRepository)
    
    companion object {
        private const val DEFAULT_PLACE_ID = "DEFAULT_PLACE_ID"
    }
    
    @Before
    fun setup() {
        coEvery {
            roomDomainRepository.insertNewClickedRestaurantToFavorite(provideClickedRestaurantEntity())
        } returns 1L
        
        coEvery {
            roomDomainRepository.removeClickedRestaurantToFavorite(provideClickedRestaurantEntity())
        } returns 1
    }
    
    @Test
    fun `nominal case - insert in favorite`() = testCoroutineRule.runTest {
        val result = useCase.invoke(true, DEFAULT_PLACE_ID)
        
        assertThat(result).isTrue()
        
        coVerify {
            roomDomainRepository.insertNewClickedRestaurantToFavorite(provideClickedRestaurantEntity())
        }
        
        confirmVerified(roomDomainRepository)
    }
    
    @Test
    fun `nominal case - remove from favorite`() = testCoroutineRule.runTest {
        val result = useCase.invoke(false, DEFAULT_PLACE_ID)
        
        assertThat(result).isTrue()
        
        coVerify {
            roomDomainRepository.removeClickedRestaurantToFavorite(provideClickedRestaurantEntity())
        }
        
        confirmVerified(roomDomainRepository)
    }
    
    private fun provideClickedRestaurantEntity() = ClickedRestaurantsEntity(
        DEFAULT_PLACE_ID
    )
}