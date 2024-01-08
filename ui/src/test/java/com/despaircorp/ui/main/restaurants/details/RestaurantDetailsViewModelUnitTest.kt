package com.despaircorp.ui.main.restaurants.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.firebase_auth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firebase_auth.model.AuthenticateUserEntity
import com.despaircorp.domain.firestore.AddCurrentEatingRestaurantUseCase
import com.despaircorp.domain.firestore.GetCoworkersForSpecificRestaurantAsFlowUseCase
import com.despaircorp.domain.firestore.IsUserEatingInClickedRestaurantUseCase
import com.despaircorp.domain.firestore.RemoveCurrentEatingRestaurantUseCase
import com.despaircorp.domain.firestore.model.CoworkersEntity
import com.despaircorp.domain.restaurants.GetRestaurantDetailsByPlaceIdUseCase
import com.despaircorp.domain.restaurants.model.RestaurantEntity
import com.despaircorp.domain.room.AddOrDeleteClickedRestaurantFromFavoriteUseCase
import com.despaircorp.domain.room.IsClickedRestaurantInFavoritesUseCase
import com.despaircorp.ui.BuildConfig
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.EquatableCallback
import com.despaircorp.ui.utils.TestCoroutineRule
import com.despaircorp.ui.utils.observeForTesting
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RestaurantDetailsViewModelUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private val savedStateHandle: SavedStateHandle = mockk()
    private val getRestaurantDetailsByPlaceIdUseCase: GetRestaurantDetailsByPlaceIdUseCase = mockk()
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase = mockk()
    private val getCoworkersForSpecificRestaurantUseCase: GetCoworkersForSpecificRestaurantAsFlowUseCase =
        mockk()
    private val isClickedRestaurantInFavoritesUseCase: IsClickedRestaurantInFavoritesUseCase =
        mockk()
    private val addOrDeleteClickedRestaurantFromFavoriteUseCase: AddOrDeleteClickedRestaurantFromFavoriteUseCase =
        mockk()
    private val isUserEatingInClickedRestaurantUseCase: IsUserEatingInClickedRestaurantUseCase =
        mockk()
    private val removeCurrentEatingRestaurantUseCase: RemoveCurrentEatingRestaurantUseCase = mockk()
    private val addCurrentEatingRestaurantUseCase: AddCurrentEatingRestaurantUseCase = mockk()
    
    private lateinit var viewModel: RestaurantDetailsViewModel
    
    companion object {
        private const val DEFAULT_PLACE_ID = "DEFAULT_PLACE_ID"
        private const val ARG_PLACE_ID = "ARG_PLACE_ID"
        private const val DEFAULT_ID = "DEFAULT_ID"
        private const val DEFAULT_NAME = "DEFAULT_NAME"
        private const val DEFAULT_PHOTO_URL = "DEFAULT_PHOTO_URL"
        private const val DEFAULT_RESTAURANTS_LATITUDE = 10.0
        private const val DEFAULT_RESTAURANTS_LONGITUDE = 20.0
        private const val DEFAULT_IS_OPENED_NOW = true
        private const val DEFAULT_WORKMATE_INSIDE = 2
        private const val DEFAULT_VICINITY = "DEFAULT_VICINITY"
        private const val DEFAULT_RATING = 3.0
        private const val DEFAULT_WEBSITE_URL = "DEFAULT_WEBSITE_URL"
        private const val DEFAULT_PHONE_NUMBER = "DEFAULT_PHONE_NUMBER"
        
        
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
        private const val DEFAULT_MAIL = "DEFAULT_MAIL"
        private const val DEFAULT_PICTURE = "DEFAULT_PICTURE"
        private const val DEFAULT_CURRENTLY_EATING = false
        private val DEFAULT_EATING_PLACE_IDE = null
    }
    
    @Before
    fun setup() {
        every { savedStateHandle.get<String>(ARG_PLACE_ID) } returns DEFAULT_PLACE_ID
        coEvery { getRestaurantDetailsByPlaceIdUseCase.invoke(DEFAULT_PLACE_ID) } returns provideRestaurantEntity()
        coEvery { getAuthenticatedUserUseCase.invoke() } returns provideAuthenticatedUserEntity()
        
        coEvery { getCoworkersForSpecificRestaurantUseCase.invoke(DEFAULT_PLACE_ID) } returns flowOf(
            provideCoworkerEntities()
        )
        coEvery { isClickedRestaurantInFavoritesUseCase.invoke(DEFAULT_PLACE_ID) } returns flowOf(
            false
        )
        coEvery { isUserEatingInClickedRestaurantUseCase.invoke(DEFAULT_PLACE_ID) } returns flowOf(
            false
        )
        coEvery { removeCurrentEatingRestaurantUseCase.invoke(DEFAULT_UID) } returns true
        coEvery {
            addCurrentEatingRestaurantUseCase.invoke(
                DEFAULT_PLACE_ID,
                DEFAULT_UID
            )
        } returns true
        coEvery {
            addOrDeleteClickedRestaurantFromFavoriteUseCase.invoke(
                true,
                DEFAULT_PLACE_ID
            )
        } returns true
        
        
        viewModel = RestaurantDetailsViewModel(
            savedStateHandle = savedStateHandle,
            getRestaurantDetailsByPlaceIdUseCase = getRestaurantDetailsByPlaceIdUseCase,
            getAuthenticatedUserUseCase = getAuthenticatedUserUseCase,
            getCoworkersForSpecificRestaurantUseCase = getCoworkersForSpecificRestaurantUseCase,
            isClickedRestaurantInFavoritesUseCase = isClickedRestaurantInFavoritesUseCase,
            addOrDeleteClickedRestaurantFromFavoriteUseCase = addOrDeleteClickedRestaurantFromFavoriteUseCase,
            isUserEatingInClickedRestaurantUseCase = isUserEatingInClickedRestaurantUseCase,
            removeCurrentEatingRestaurantUseCase = removeCurrentEatingRestaurantUseCase,
            addCurrentEatingRestaurantUseCase = addCurrentEatingRestaurantUseCase
        )
    }
    
    @Test
    fun `nominal case - get details with coworkers list, not selected restaurant, not in favorite`() =
        testCoroutineRule.runTest {
            
            viewModel.viewState.observeForTesting(this) {
                assertThat(it.value).isEqualTo(provideRestaurantDetailsViewState())
            }
        }
    
    @Test
    fun `nominal case - get details with coworkers list, selected restaurant, in favorite`() =
        testCoroutineRule.runTest {
            coEvery { isClickedRestaurantInFavoritesUseCase.invoke(DEFAULT_PLACE_ID) } returns flowOf(
                true
            )
            
            coEvery { isUserEatingInClickedRestaurantUseCase.invoke(DEFAULT_PLACE_ID) } returns flowOf(
                true
            )
            
            viewModel.viewState.observeForTesting(this) {
                assertThat(it.value).isEqualTo(
                    provideRestaurantDetailsViewState().copy(
                        fabIcon = R.drawable.check_mark,
                        likeIcon = R.drawable.star__2_
                    )
                )
            }
        }
    
    @Test
    fun `nominal case - don't eat here`() = testCoroutineRule.runTest {
        coEvery { isUserEatingInClickedRestaurantUseCase.invoke(DEFAULT_PLACE_ID) } returns flowOf(
            true
        )
        coEvery { removeCurrentEatingRestaurantUseCase.invoke(DEFAULT_UID) } returns true
        
        viewModel.viewState.observeForTesting(this) {
            it.value?.onFabClicked?.invoke()
            assertThat(it.value).isEqualTo(provideRestaurantDetailsViewState().copy(fabIcon = R.drawable.check_mark))
        }
    }
    
    @Test
    fun `error case - don't eat here`() = testCoroutineRule.runTest {
        coEvery { isUserEatingInClickedRestaurantUseCase.invoke(DEFAULT_PLACE_ID) } returns flowOf(
            true
        )
        coEvery { removeCurrentEatingRestaurantUseCase.invoke(DEFAULT_UID) } returns false
        
        viewModel.viewState.observeForTesting(this) {
            it.value?.onFabClicked?.invoke()
            assertThat(it.value).isEqualTo(provideRestaurantDetailsViewState().copy(fabIcon = R.drawable.check_mark))
        }
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(
                RestaurantDetailsAction.Error(
                    R.string.error_occurred
                )
            )
        }
        
    }
    
    @Test
    fun `nominal case - eat here`() = testCoroutineRule.runTest {
        viewModel.viewState.observeForTesting(this) {
            it.value?.onFabClicked?.invoke()
            assertThat(it.value).isEqualTo(provideRestaurantDetailsViewState())
        }
    }
    
    @Test
    fun `error case - eat here`() = testCoroutineRule.runTest {
        coEvery {
            addCurrentEatingRestaurantUseCase.invoke(
                DEFAULT_PLACE_ID,
                DEFAULT_UID
            )
        } returns false
        
        viewModel.viewState.observeForTesting(this) {
            it.value?.onFabClicked?.invoke()
            assertThat(it.value).isEqualTo(provideRestaurantDetailsViewState())
        }
    }
    
    @Test
    fun `nominal case - add to favorite`() =
        testCoroutineRule.runTest {
            
            viewModel.viewState.observeForTesting(this) {
                it.value?.onLikeClicked?.invoke()
                assertThat(it.value).isEqualTo(provideRestaurantDetailsViewState())
            }
            
            
        }
    
    @Test
    fun `error case - add to favorite`() =
        testCoroutineRule.runTest {
            coEvery {
                addOrDeleteClickedRestaurantFromFavoriteUseCase.invoke(
                    true,
                    DEFAULT_PLACE_ID
                )
            } returns false
            
            viewModel.viewState.observeForTesting(this) {
                it.value?.onLikeClicked?.invoke()
                assertThat(it.value).isEqualTo(provideRestaurantDetailsViewState())
            }
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(
                    RestaurantDetailsAction.Error(
                        R.string.error_occurred
                    )
                )
            }
        }
    
    @Test
    fun `nominal case - remove to favorite `() =
        testCoroutineRule.runTest {
            coEvery { isClickedRestaurantInFavoritesUseCase.invoke(DEFAULT_PLACE_ID) } returns flowOf(
                true
            )
            coEvery {
                addOrDeleteClickedRestaurantFromFavoriteUseCase.invoke(
                    false,
                    DEFAULT_PLACE_ID
                )
            } returns true
            viewModel.viewState.observeForTesting(this) {
                it.value?.onLikeClicked?.invoke()
                assertThat(it.value).isEqualTo(
                    provideRestaurantDetailsViewState().copy(
                        likeIcon = R.drawable.star__2_
                    )
                )
            }
        }
    
    @Test
    fun `error case - remove from favorite `() =
        testCoroutineRule.runTest {
            coEvery { isClickedRestaurantInFavoritesUseCase.invoke(DEFAULT_PLACE_ID) } returns flowOf(
                true
            )
            coEvery {
                addOrDeleteClickedRestaurantFromFavoriteUseCase.invoke(
                    false,
                    DEFAULT_PLACE_ID
                )
            } returns false
            
            viewModel.viewState.observeForTesting(this) {
                it.value?.onLikeClicked?.invoke()
                assertThat(it.value).isEqualTo(
                    provideRestaurantDetailsViewState().copy(
                        likeIcon = R.drawable.star__2_
                    )
                )
            }
            
            viewModel.viewAction.observeForTesting(this) {
                assertThat(it.value?.getContentIfNotHandled()).isEqualTo(
                    RestaurantDetailsAction.Error(
                        R.string.error_occurred
                    )
                )
            }
        }
    
    private fun provideRestaurantDetailsViewState() = RestaurantDetailsViewState(
        DEFAULT_NAME,
        DEFAULT_RATING,
        "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1920&maxheigth=1080&photo_reference=${DEFAULT_PHOTO_URL}&key=${BuildConfig.MAPS_API_KEY}",
        DEFAULT_VICINITY,
        DEFAULT_WEBSITE_URL,
        DEFAULT_PHONE_NUMBER,
        provideRestaurantDetailsCoworkerViewStateItems(),
        isSnackBarVisible = false,
        snackBarMessage = null,
        R.color.rusty_red,
        R.drawable.cursor,
        R.drawable.star__1_,
        EquatableCallback("onFabClicked") {},
        EquatableCallback("onLikeClicked") {}
    )
    
    
    private fun provideRestaurantDetailsCoworkerViewStateItems() = List(3) {
        RestaurantDetailsCoworkerViewStateItems(
            provideCoworkerEntity().uid,
            provideCoworkerEntity().pictureUrl,
            provideCoworkerEntity().name
        )
    }
    
    private fun provideCoworkerEntity() = CoworkersEntity(
        uid = DEFAULT_UID,
        isEating = DEFAULT_CURRENTLY_EATING,
        eatingPlaceId = DEFAULT_PLACE_ID,
        pictureUrl = DEFAULT_PICTURE,
        name = DEFAULT_DISPLAY_NAME
    )
    
    private fun provideCoworkerEntities() = List(3) {
        CoworkersEntity(
            uid = DEFAULT_UID,
            isEating = DEFAULT_CURRENTLY_EATING,
            eatingPlaceId = DEFAULT_PLACE_ID,
            pictureUrl = DEFAULT_PICTURE,
            name = DEFAULT_DISPLAY_NAME
        )
    }
    
    private fun provideAuthenticatedUserEntity() = AuthenticateUserEntity(
        picture = DEFAULT_PICTURE,
        displayName = DEFAULT_DISPLAY_NAME,
        mailAddress = DEFAULT_MAIL,
        uid = DEFAULT_UID,
        currentlyEating = DEFAULT_CURRENTLY_EATING,
        eatingPlaceId = DEFAULT_EATING_PLACE_IDE
    )
    
    private fun provideRestaurantEntity() = RestaurantEntity(
        id = DEFAULT_ID,
        name = DEFAULT_NAME,
        photoUrl = DEFAULT_PHOTO_URL,
        latitude = DEFAULT_RESTAURANTS_LATITUDE,
        longitude = DEFAULT_RESTAURANTS_LONGITUDE,
        isOpenedNow = DEFAULT_IS_OPENED_NOW,
        workmateInside = DEFAULT_WORKMATE_INSIDE,
        vicinity = DEFAULT_VICINITY,
        rating = DEFAULT_RATING,
        webSiteUrl = DEFAULT_WEBSITE_URL,
        phoneNumber = DEFAULT_PHONE_NUMBER
    )
}