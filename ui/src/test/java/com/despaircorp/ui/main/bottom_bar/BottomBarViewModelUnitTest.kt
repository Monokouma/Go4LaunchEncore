package com.despaircorp.ui.main.bottom_bar

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.firebase_auth.DisconnectUserUseCase
import com.despaircorp.domain.firebase_auth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firestore.GetCoworkersForSpecificRestaurantAsFlowUseCase
import com.despaircorp.domain.firestore.GetFirestoreUserAsFlowUseCase
import com.despaircorp.domain.firestore.model.CoworkersEntity
import com.despaircorp.domain.firestore.model.FirestoreUserEntity
import com.despaircorp.domain.location.GetUserLocationEntityAsFlowUseCase
import com.despaircorp.domain.location.model.LocationEntity
import com.despaircorp.domain.notifications.CreateNotificationChannelUseCase
import com.despaircorp.domain.restaurants.GetRestaurantDetailsByPlaceIdUseCase
import com.despaircorp.domain.restaurants.model.RestaurantEntity
import com.despaircorp.domain.workers.EnqueueLaunchNotificationWorker
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.TestCoroutineRule
import com.despaircorp.ui.utils.observeForTesting
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BottomBarViewModelUnitTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase = mockk()
    private val getFirestoreUserUseCase: GetFirestoreUserAsFlowUseCase = mockk()
    private val disconnectUserUseCase: DisconnectUserUseCase = mockk()
    private val enqueueLaunchNotificationWorker: EnqueueLaunchNotificationWorker = mockk()
    private val createNotificationChannelUseCase: CreateNotificationChannelUseCase = mockk()
    private val getUserLocationEntityAsFlowUseCase: GetUserLocationEntityAsFlowUseCase = mockk()
    
    private val getRestaurantDetailsByPlaceIdUseCase: GetRestaurantDetailsByPlaceIdUseCase = mockk()
    private val application: Application = mockk()
    private val getCoworkersForSpecificRestaurantAsFlowUseCase: GetCoworkersForSpecificRestaurantAsFlowUseCase =
        mockk()
    private lateinit var viewModel: BottomBarViewModel
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
        private const val DEFAULT_EMAIL = "DEFAULT_EMAIL"
        private const val DEFAULT_PICTURE = "DEFAULT_PICTURE"
        private const val DEFAULT_CURRENTLY_EATING = true
        private const val DEFAULT_EATING_PLACE_IDE = "DEFAULT_EATING_PLACE_IDE"
        private const val DEFAULT_ONLINE = true
        private val DEFAULT_LATLNG = LatLng(49.857920, 1.295048)
        private val DEFAULT_LAT_LNG_BOUND = LatLngBounds(
            LatLng(49.84892678394081, 1.2810982218889557),
            LatLng(49.86691321605919, 1.3089977781110442)
        )
        
        private const val DEFAULT_NAME = "DEFAULT_NAME"
        private const val DEFAULT_PHOTO_URL = "DEFAULT_PHOTO_URL"
        private const val DEFAULT_RESTAURANTS_LATITUDE = 10.0
        private const val DEFAULT_RESTAURANTS_LONGITUDE = 20.0
        private const val DEFAULT_IS_OPENED_NOW = true
        private const val DEFAULT_WORKMATE_INSIDE = 2
        private const val DEFAULT_VICINITY = "DEFAULT_VICINITY"
        private const val DEFAULT_RATING = 3.0
    }
    
    @Before
    fun setup() {
        coEvery { getAuthenticatedUserUseCase.invoke().uid } returns DEFAULT_UID
        coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID) } returns flowOf(
            FirestoreUserEntity(
                picture = DEFAULT_PICTURE,
                displayName = DEFAULT_DISPLAY_NAME,
                mailAddress = DEFAULT_EMAIL,
                uid = DEFAULT_UID,
                currentlyEating = DEFAULT_CURRENTLY_EATING,
                eatingPlaceId = DEFAULT_EATING_PLACE_IDE,
                online = DEFAULT_ONLINE
            )
        )
        
        coEvery { disconnectUserUseCase.invoke() } returns true
        
        coJustRun { enqueueLaunchNotificationWorker.invoke() }
        
        coEvery { getUserLocationEntityAsFlowUseCase.invoke() } returns flowOf(provideLocationEntity())
        
        coJustRun { createNotificationChannelUseCase.invoke() }
        
        coEvery { getRestaurantDetailsByPlaceIdUseCase.invoke(DEFAULT_EATING_PLACE_IDE) } returns provideRestaurantsEntity()
        coEvery { getCoworkersForSpecificRestaurantAsFlowUseCase.invoke(DEFAULT_EATING_PLACE_IDE) } returns flowOf(
            provideCoworkersEntities()
        )
        
        every { application.getString(R.string.you_eat_at) } returns "You eat at"
        every { application.getString(R.string.no_restaurant_selected) } returns "No lunch planned"
        every { application.getString(R.string.alone) } returns "Alone"
        every { application.getString(R.string.with) } returns "with"
        
        viewModel = BottomBarViewModel(
            getAuthenticatedUserUseCase = getAuthenticatedUserUseCase,
            disconnectUserUseCase = disconnectUserUseCase,
            getFirestoreUserAsFlowUseCase = getFirestoreUserUseCase,
            enqueueLaunchNotificationWorker = enqueueLaunchNotificationWorker,
            createNotificationChannelUseCase = createNotificationChannelUseCase,
            getUserLocationEntityAsFlowUseCase = getUserLocationEntityAsFlowUseCase,
            getRestaurantDetailsByPlaceIdUseCase,
            application,
            getCoworkersForSpecificRestaurantAsFlowUseCase
        )
    }
    
    @Test
    fun `nominal case - infos are correct and sign out success`() = testCoroutineRule.runTest {
        
        viewModel.viewState.observeForTesting(this) {
            assertThat(it.value).isEqualTo(
                BottomBarViewState(
                    username = DEFAULT_DISPLAY_NAME,
                    emailAddress = DEFAULT_EMAIL,
                    userImage = DEFAULT_PICTURE,
                    DEFAULT_LATLNG,
                    "You eat at $DEFAULT_NAME with$DEFAULT_DISPLAY_NAME, $DEFAULT_DISPLAY_NAME, $DEFAULT_DISPLAY_NAME",
                    DEFAULT_LAT_LNG_BOUND
                )
            )
        }
        
        viewModel.onDisconnectUser()
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(BottomBarAction.OnDisconnect)
        }
    }
    
    @Test
    fun `nominal case - infos are correct and sign out failure`() = testCoroutineRule.runTest {
        coEvery { disconnectUserUseCase.invoke() } returns false
        
        viewModel.viewState.observeForTesting(this) {
            assertThat(it.value).isEqualTo(
                BottomBarViewState(
                    username = DEFAULT_DISPLAY_NAME,
                    emailAddress = DEFAULT_EMAIL,
                    userImage = DEFAULT_PICTURE,
                    userLatLn = DEFAULT_LATLNG,
                    "You eat at $DEFAULT_NAME with$DEFAULT_DISPLAY_NAME, $DEFAULT_DISPLAY_NAME, $DEFAULT_DISPLAY_NAME",
                    DEFAULT_LAT_LNG_BOUND
                )
            )
        }
        
        viewModel.onDisconnectUser()
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(BottomBarAction.Error(message = R.string.error_occurred))
        }
    }
    
    @Test
    fun `nominal case - view state with no eating user`() = testCoroutineRule.runTest {
        coEvery { disconnectUserUseCase.invoke() } returns false
        coEvery { getFirestoreUserUseCase.invoke(DEFAULT_UID) } returns flowOf(
            FirestoreUserEntity(
                picture = DEFAULT_PICTURE,
                displayName = DEFAULT_DISPLAY_NAME,
                mailAddress = DEFAULT_EMAIL,
                uid = DEFAULT_UID,
                currentlyEating = false,
                eatingPlaceId = DEFAULT_EATING_PLACE_IDE,
                online = DEFAULT_ONLINE
            )
        )
        
        viewModel.viewState.observeForTesting(this) {
            assertThat(it.value).isEqualTo(
                BottomBarViewState(
                    username = DEFAULT_DISPLAY_NAME,
                    emailAddress = DEFAULT_EMAIL,
                    userImage = DEFAULT_PICTURE,
                    userLatLn = DEFAULT_LATLNG,
                    "No lunch planned",
                    DEFAULT_LAT_LNG_BOUND
                )
            )
        }
        
        viewModel.onDisconnectUser()
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(BottomBarAction.Error(message = R.string.error_occurred))
        }
    }
    
    @Test
    fun `nominal case - view state with one eating coworker`() = testCoroutineRule.runTest {
        coEvery { disconnectUserUseCase.invoke() } returns false
        coEvery { getCoworkersForSpecificRestaurantAsFlowUseCase.invoke(DEFAULT_EATING_PLACE_IDE) } returns flowOf(
            emptyList()
        )
        
        viewModel.viewState.observeForTesting(this) {
            assertThat(it.value).isEqualTo(
                BottomBarViewState(
                    username = DEFAULT_DISPLAY_NAME,
                    emailAddress = DEFAULT_EMAIL,
                    userImage = DEFAULT_PICTURE,
                    userLatLn = DEFAULT_LATLNG,
                    "You eat at $DEFAULT_NAME Alone",
                    DEFAULT_LAT_LNG_BOUND
                )
            )
        }
        
        viewModel.onDisconnectUser()
        
        viewModel.viewAction.observeForTesting(this) {
            assertThat(it.value?.getContentIfNotHandled()).isEqualTo(BottomBarAction.Error(message = R.string.error_occurred))
        }
    }
    
    private fun provideLocationEntity() = LocationEntity(
        DEFAULT_LATLNG
    )
    
    private fun provideRestaurantsEntity() = RestaurantEntity(
        id = DEFAULT_EATING_PLACE_IDE,
        name = DEFAULT_NAME,
        photoUrl = DEFAULT_PHOTO_URL,
        latitude = DEFAULT_RESTAURANTS_LATITUDE,
        longitude = DEFAULT_RESTAURANTS_LONGITUDE,
        isOpenedNow = DEFAULT_IS_OPENED_NOW,
        workmateInside = DEFAULT_WORKMATE_INSIDE,
        vicinity = DEFAULT_VICINITY,
        rating = DEFAULT_RATING,
        null,
        null
    )
    
    private fun provideCoworkersEntities() = List(3) {
        CoworkersEntity(
            uid = DEFAULT_UID,
            isEating = DEFAULT_CURRENTLY_EATING,
            eatingPlaceId = DEFAULT_EATING_PLACE_IDE,
            pictureUrl = DEFAULT_PICTURE,
            name = DEFAULT_DISPLAY_NAME
        )
    }
}









