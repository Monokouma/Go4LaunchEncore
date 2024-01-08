package com.despaircorp.data.workers

import android.app.Application
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.data.R
import com.despaircorp.data.utils.TestCoroutineRule
import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firestore.FirestoreDomainRepository
import com.despaircorp.domain.firestore.GetCoworkersForSpecificRestaurantAsFlowUseCase
import com.despaircorp.domain.firestore.model.CoworkersEntity
import com.despaircorp.domain.notifications.NotificationDomainRepository
import com.despaircorp.domain.restaurants.GetRestaurantDetailsByPlaceIdUseCase
import com.despaircorp.domain.restaurants.model.RestaurantEntity
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NotificationWorkerUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val application: Application = mockk()
    
    private val workerParameters: WorkerParameters = mockk()
    
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository = mockk()
    private val firestoreDomainRepository: FirestoreDomainRepository = mockk()
    private val notificationDomainRepository: NotificationDomainRepository = mockk()
    private val getRestaurantDetailsByPlaceIdUseCase: GetRestaurantDetailsByPlaceIdUseCase = mockk()
    private val getCoworkersForSpecificRestaurantAsFlowUseCase: GetCoworkersForSpecificRestaurantAsFlowUseCase =
        mockk()
    
    private lateinit var notificationWorker: NotificationWorker
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_PLACE_ID = "DEFAULT_PLACE_ID"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
        private const val DEFAULT_IS_CURRENTLY_EATING = false
        
        private const val DEFAULT_ID = "DEFAULT_ID"
        private const val DEFAULT_NAME = "DEFAULT_NAME"
        private const val DEFAULT_PHOTO_URL = "DEFAULT_PHOTO_URL"
        private const val DEFAULT_RESTAURANTS_LATITUDE = 10.0
        private const val DEFAULT_RESTAURANTS_LONGITUDE = 20.0
        private const val DEFAULT_IS_OPENED_NOW = true
        private const val DEFAULT_WORKMATE_INSIDE = 2
        private const val DEFAULT_VICINITY = "DEFAULT_VICINITY"
        private const val DEFAULT_RATING = 3.0
        
        private const val DEFAULT_PICTURE = "DEFAULT_PICTURE"
        private const val DEFAULT_CURRENTLY_EATING = true
        private const val DEFAULT_EATING_PLACE_ID = "DEFAULT_EATING_PLACE_ID"
    }
    
    @Before
    fun setup() {
        every { application.applicationContext } returns mockk()
        every { workerParameters.taskExecutor.serialTaskExecutor } returns mockk()
        coEvery { firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid } returns DEFAULT_UID
        coEvery { firestoreDomainRepository.getUser(DEFAULT_UID).displayName } returns DEFAULT_DISPLAY_NAME
        coEvery { firestoreDomainRepository.getUser(DEFAULT_UID).uid } returns DEFAULT_UID
        coEvery { firestoreDomainRepository.getUser(DEFAULT_UID).currentlyEating } returns DEFAULT_IS_CURRENTLY_EATING
        
        justRun {
            notificationDomainRepository.notify(
                DEFAULT_DISPLAY_NAME,
                DEFAULT_UID
            )
        }
        
        every { application.applicationContext.getString(R.string.you_eat_at) } returns "You eat at"
        every { application.applicationContext.getString(R.string.no_restaurant_selected) } returns "No selection"
        every { application.applicationContext.getString(R.string.alone) } returns "alone"
        every { application.applicationContext.getString(R.string.with) } returns "with"
        
        coJustRun { notificationDomainRepository.notify(DEFAULT_DISPLAY_NAME, "No selection") }
        
        notificationWorker = NotificationWorker(
            context = application.applicationContext,
            workerParams = workerParameters,
            firebaseAuthDomainRepository = firebaseAuthDomainRepository,
            firestoreDomainRepository = firestoreDomainRepository,
            notificationDomainRepository = notificationDomainRepository,
            getRestaurantDetailsByPlaceIdUseCase,
            getCoworkersForSpecificRestaurantAsFlowUseCase
        )
    }
    
    @Test
    fun `nominal case - success with no restaurant selected`() = testCoroutineRule.runTest {
        val result = notificationWorker.doWork()
        
        assertThat(result).isEqualTo(ListenableWorker.Result.success())
        
        coVerify {
            firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid
            firestoreDomainRepository.getUser(DEFAULT_UID).displayName
            notificationDomainRepository.notify(
                DEFAULT_DISPLAY_NAME,
                "No selection"
            )
        }
        
        confirmVerified(
            firestoreDomainRepository,
            firebaseAuthDomainRepository,
            notificationDomainRepository
        )
    }
    
    @Test
    fun `edge case - success with restaurant selected`() = testCoroutineRule.runTest {
        coEvery { firestoreDomainRepository.getUser(DEFAULT_UID).currentlyEating } returns true
        coEvery { firestoreDomainRepository.getUser(DEFAULT_UID).eatingPlaceId } returns DEFAULT_PLACE_ID
        coEvery { getRestaurantDetailsByPlaceIdUseCase.invoke(DEFAULT_PLACE_ID) } returns provideRestaurantsEntity()
        coEvery { getCoworkersForSpecificRestaurantAsFlowUseCase.invoke(DEFAULT_PLACE_ID) } returns flowOf(
            provideCoworkerEntities()
        )
        coJustRun {
            notificationDomainRepository.notify(
                DEFAULT_DISPLAY_NAME,
                "You eat at $DEFAULT_NAME with$DEFAULT_DISPLAY_NAME, $DEFAULT_DISPLAY_NAME, $DEFAULT_DISPLAY_NAME"
            )
        }
        
        
        val result = notificationWorker.doWork()
        
        assertThat(result).isEqualTo(ListenableWorker.Result.success())
        
        coVerify {
            firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid
            firestoreDomainRepository.getUser(DEFAULT_UID).displayName
            
            notificationDomainRepository.notify(
                DEFAULT_DISPLAY_NAME,
                "You eat at $DEFAULT_NAME with$DEFAULT_DISPLAY_NAME, $DEFAULT_DISPLAY_NAME, $DEFAULT_DISPLAY_NAME"
            )
        }
        
        confirmVerified(
            firestoreDomainRepository,
            firebaseAuthDomainRepository,
            notificationDomainRepository
        )
    }
    
    @Test
    fun `edge case - success alone cowerker`() = testCoroutineRule.runTest {
        coEvery { firestoreDomainRepository.getUser(DEFAULT_UID).currentlyEating } returns true
        coEvery { firestoreDomainRepository.getUser(DEFAULT_UID).eatingPlaceId } returns DEFAULT_PLACE_ID
        coEvery { getRestaurantDetailsByPlaceIdUseCase.invoke(DEFAULT_PLACE_ID) } returns provideRestaurantsEntity()
        coEvery { getCoworkersForSpecificRestaurantAsFlowUseCase.invoke(DEFAULT_PLACE_ID) } returns flowOf(
            emptyList()
        )
        
        coJustRun {
            notificationDomainRepository.notify(
                DEFAULT_DISPLAY_NAME,
                "You eat at $DEFAULT_NAME alone"
            )
        }
        
        
        val result = notificationWorker.doWork()
        
        assertThat(result).isEqualTo(ListenableWorker.Result.success())
        
        coVerify {
            firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid
            firestoreDomainRepository.getUser(DEFAULT_UID).displayName
            
            notificationDomainRepository.notify(
                DEFAULT_DISPLAY_NAME,
                "You eat at $DEFAULT_NAME alone"
            )
        }
        
        confirmVerified(
            firestoreDomainRepository,
            firebaseAuthDomainRepository,
            notificationDomainRepository
        )
    }
    
    //Region OUT start
    private fun provideRestaurantsEntity() = RestaurantEntity(
        id = DEFAULT_ID,
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
    
    private fun provideCoworkerEntities() = List(3) {
        CoworkersEntity(
            uid = DEFAULT_UID,
            isEating = DEFAULT_CURRENTLY_EATING,
            eatingPlaceId = DEFAULT_EATING_PLACE_ID,
            pictureUrl = DEFAULT_PICTURE,
            name = DEFAULT_DISPLAY_NAME
        )
    }
    //Region OUT end
}