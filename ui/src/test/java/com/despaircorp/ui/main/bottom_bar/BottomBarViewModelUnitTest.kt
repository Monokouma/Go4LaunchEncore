package com.despaircorp.ui.main.bottom_bar

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.firebase_auth.DisconnectUserUseCase
import com.despaircorp.domain.firebase_auth.GetAuthenticatedUserUseCase
import com.despaircorp.domain.firestore.GetFirestoreUserAsFlowUseCase
import com.despaircorp.domain.firestore.model.FirestoreUserEntity
import com.despaircorp.domain.location.GetUserLocationEntityAsFlowUseCase
import com.despaircorp.domain.location.model.LocationEntity
import com.despaircorp.domain.notifications.CreateNotificationChannelUseCase
import com.despaircorp.domain.workers.EnqueueLaunchNotificationWorker
import com.despaircorp.ui.R
import com.despaircorp.ui.utils.TestCoroutineRule
import com.despaircorp.ui.utils.observeForTesting
import com.google.android.gms.maps.model.LatLng
import io.mockk.coEvery
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
    
    private lateinit var viewModel: BottomBarViewModel
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
        private const val DEFAULT_EMAIL = "DEFAULT_EMAIL"
        private const val DEFAULT_PICTURE = "DEFAULT_PICTURE"
        private const val DEFAULT_CURRENTLY_EATING = false
        private val DEFAULT_EATING_PLACE_IDE = null
        private const val DEFAULT_ONLINE = true
        private val DEFAULT_LATLNG = LatLng(49.857920, 1.295048)
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
        
        coEvery { enqueueLaunchNotificationWorker.invoke() } returns true
        
        coEvery { getUserLocationEntityAsFlowUseCase.invoke() } returns flowOf(provideLocationEntity())
        
        coEvery { createNotificationChannelUseCase.invoke() } returns true
        
        viewModel = BottomBarViewModel(
            getAuthenticatedUserUseCase = getAuthenticatedUserUseCase,
            disconnectUserUseCase = disconnectUserUseCase,
            getFirestoreUserAsFlowUseCase = getFirestoreUserUseCase,
            enqueueLaunchNotificationWorker = enqueueLaunchNotificationWorker,
            createNotificationChannelUseCase = createNotificationChannelUseCase,
            getUserLocationEntityAsFlowUseCase = getUserLocationEntityAsFlowUseCase
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
                    DEFAULT_LATLNG
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
                    userLatLn = DEFAULT_LATLNG
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
}









