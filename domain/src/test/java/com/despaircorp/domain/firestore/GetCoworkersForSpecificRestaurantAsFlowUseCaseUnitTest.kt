package com.despaircorp.domain.firestore

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firestore.model.CoworkersEntity
import com.despaircorp.domain.firestore.model.FirestoreUserEntity
import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetCoworkersForSpecificRestaurantAsFlowUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val firestoreDomainRepository: FirestoreDomainRepository = mockk()
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository = mockk()
    
    private val useCase = GetCoworkersForSpecificRestaurantAsFlowUseCase(
        firestoreDomainRepository,
        firebaseAuthDomainRepository
    )
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_MAIL = "DEFAULT_MAIL"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
        private const val DEFAULT_PICTURE = "DEFAULT_PICTURE"
        private const val DEFAULT_IS_ONLINE = true
        private const val DEFAULT_CURRENTLY_EATING = true
        private const val DEFAULT_PLACE_ID = "DEFAULT_PLACE_ID"
        
    }
    
    @Before
    fun setup() {
        coEvery { firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid } returns DEFAULT_UID
        coEvery { firestoreDomainRepository.getUser(DEFAULT_UID) } returns provideFirestoreUserEntity()
        coEvery { firestoreDomainRepository.getAllFirestoreUsersAsFlow() } returns provideFirestoreUserEntitiesAsFlow()
        
    }
    
    @Test
    fun `nominal case - get coworker list`() = testCoroutineRule.runTest {
        useCase.invoke(DEFAULT_PLACE_ID).test {
            val result = awaitItem()
            awaitComplete()
            
            assertThat(result).isEqualTo(provideCoworkerEntities())
            
            coVerify {
                firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid
                firestoreDomainRepository.getUser(DEFAULT_UID)
                firestoreDomainRepository.getAllFirestoreUsersAsFlow()
            }
            
            confirmVerified(
                firestoreDomainRepository,
                firebaseAuthDomainRepository
            )
        }
    }
    
    private fun provideCoworkerEntities() = List(3) {
        CoworkersEntity(
            uid = DEFAULT_UID,
            isEating = DEFAULT_CURRENTLY_EATING,
            eatingPlaceId = DEFAULT_PLACE_ID,
            pictureUrl = DEFAULT_PICTURE,
            name = DEFAULT_DISPLAY_NAME
        )
    }
    
    private fun provideFirestoreUserEntity() = FirestoreUserEntity(
        picture = DEFAULT_PICTURE,
        displayName = DEFAULT_DISPLAY_NAME,
        mailAddress = DEFAULT_MAIL,
        uid = DEFAULT_UID,
        currentlyEating = DEFAULT_CURRENTLY_EATING,
        eatingPlaceId = DEFAULT_PLACE_ID,
        online = DEFAULT_IS_ONLINE
    )
    
    private fun provideFirestoreUserEntitiesAsFlow(): Flow<List<FirestoreUserEntity>> = flow {
        val list = mutableListOf<FirestoreUserEntity>()
        for (i in 3 downTo 0) {
            list.add(
                FirestoreUserEntity(
                    picture = DEFAULT_PICTURE,
                    displayName = DEFAULT_DISPLAY_NAME,
                    mailAddress = DEFAULT_MAIL,
                    uid = DEFAULT_UID,
                    currentlyEating = DEFAULT_CURRENTLY_EATING,
                    eatingPlaceId = DEFAULT_PLACE_ID,
                    online = DEFAULT_IS_ONLINE
                )
            )
        }
        emit(list.reversed())
    }
}