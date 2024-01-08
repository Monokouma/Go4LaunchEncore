package com.despaircorp.domain.room

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isTrue
import com.despaircorp.domain.room.model.ClickedRestaurantsEntity
import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class IsClickedRestaurantInFavoritesUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val roomDomainRepository: RoomDomainRepository = mockk()
    
    private val useCase = IsClickedRestaurantInFavoritesUseCase(roomDomainRepository)
    
    companion object {
        private const val DEFAULT_PLACE_ID = "DEFAULT_PLACE_ID"
    }
    
    @Before
    fun setup() {
        coEvery { roomDomainRepository.getFavoriteRestaurants() } returns flowOf(
            provideClickedRestaurantEntities()
        )
    }
    
    @Test
    fun `nominal case - restaurant is in favorite list`() = testCoroutineRule.runTest {
        useCase.invoke(DEFAULT_PLACE_ID).test {
            val result = awaitItem()
            awaitComplete()
            
            assertThat(result).isTrue()
            
            coVerify {
                roomDomainRepository.getFavoriteRestaurants()
            }
            
            confirmVerified(roomDomainRepository)
        }
    }
    
    private fun provideClickedRestaurantEntities() = List(3) {
        provideClickedRestaurantEntity()
    }
    
    private fun provideClickedRestaurantEntity() = ClickedRestaurantsEntity(
        DEFAULT_PLACE_ID
    )
}