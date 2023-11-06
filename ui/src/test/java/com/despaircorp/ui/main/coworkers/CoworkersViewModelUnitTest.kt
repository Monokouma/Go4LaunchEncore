package com.despaircorp.ui.main.coworkers

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.firestore.GetAllCoworkersUseCase
import com.despaircorp.domain.firestore.model.CoworkersEntity
import com.despaircorp.domain.restaurants.GetRestaurantDetailsByPlaceIdUseCase
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.TestCoroutineRule
import com.despaircorp.ui.utils.observeForTesting
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoworkersViewModelUnitTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val getAllUserListUseCase: GetAllCoworkersUseCase = mockk()
    private val application: Application = mockk()
    private val getRestaurantDetailsByPlaceIdUseCase: GetRestaurantDetailsByPlaceIdUseCase = mockk()
    
    private lateinit var viewModel: CoworkersViewModel
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
        private const val DEFAULT_NOT_DECIDED = "DEFAULT_NOT_DECIDED"
        private const val DEFAULT_PICTURE = "DEFAULT_PICTURE"
        private const val DEFAULT_CURRENTLY_EATING = true
        private const val DEFAULT_EATING_PLACE_IDE = "DEFAULT_EATING_PLACE_IDE"
        private const val DEFAULT_PLACE_NAME = "DEFAULT_PLACE_NAME"
        
    }
    
    @Before
    fun setup() {
        coEvery { getAllUserListUseCase.invoke() } returns flowOf(provideCoworkerEntities())
        coEvery {
            getRestaurantDetailsByPlaceIdUseCase.invoke(
                DEFAULT_EATING_PLACE_IDE
            )?.name
        } returns DEFAULT_PLACE_NAME
        
        
        coEvery {
            application.getString(
                R.string.is_eating,
                DEFAULT_PLACE_NAME
            )
        } returns DEFAULT_PLACE_NAME
        
        every { application.getString(R.string.not_decided) } returns DEFAULT_NOT_DECIDED
        
        
        viewModel = CoworkersViewModel(
            getAllUserListUseCase = getAllUserListUseCase,
            application = application,
            getRestaurantDetailsByPlaceIdUseCase = getRestaurantDetailsByPlaceIdUseCase
        )
    }
    
    @Test
    fun `nominal case - get coworkers eating`() = testCoroutineRule.runTest {
        viewModel.viewState.observeForTesting(this) {
            assertThat(it.value).isEqualTo(
                provideCoworkersViewState()
            )
        }
    }
    
    
    private fun provideCoworkersViewState() = CoworkersViewState(
        coworkersViewStateItems = provideCoworkersViewStateItems(),
        isSpinnerVisible = false
    )
    
    private fun provideCoworkersViewStateItems() = List(3) {
        if (it == 2) {
            CoworkersViewStateItems(
                picture = DEFAULT_PICTURE,
                eatingMessage = "$DEFAULT_DISPLAY_NAME $DEFAULT_NOT_DECIDED",
                id = DEFAULT_UID,
                isEating = false,
                textColor = R.color.gray
            )
        } else {
            CoworkersViewStateItems(
                picture = DEFAULT_PICTURE,
                eatingMessage = "$DEFAULT_DISPLAY_NAME $DEFAULT_PLACE_NAME",
                id = DEFAULT_UID,
                isEating = DEFAULT_CURRENTLY_EATING,
                textColor = R.color.baby_powder
            )
        }
        
    }
    
    private fun provideCoworkerEntities() = List(3) {
        if (it == 2) {
            CoworkersEntity(
                uid = DEFAULT_UID,
                isEating = false,
                eatingPlaceId = null,
                pictureUrl = DEFAULT_PICTURE,
                name = DEFAULT_DISPLAY_NAME
            )
        } else {
            CoworkersEntity(
                uid = DEFAULT_UID,
                isEating = DEFAULT_CURRENTLY_EATING,
                eatingPlaceId = DEFAULT_EATING_PLACE_IDE,
                pictureUrl = DEFAULT_PICTURE,
                name = DEFAULT_DISPLAY_NAME
            )
        }
        
    }
}