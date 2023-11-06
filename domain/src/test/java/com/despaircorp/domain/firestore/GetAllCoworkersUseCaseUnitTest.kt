package com.despaircorp.domain.firestore

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firebase_auth.model.AuthenticateUserEntity
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

class GetAllCoworkersUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val firestoreDomainRepository: FirestoreDomainRepository = mockk()
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository = mockk()
    
    private val useCase = GetAllCoworkersUseCase(
        firestoreDomainRepository = firestoreDomainRepository,
        firebaseAuthDomainRepository = firebaseAuthDomainRepository
    )
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_OTHER_UID = "DEFAULT_OTHER_UID"
        private const val DEFAULT_MAIL = "DEFAULT_MAIL"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
        private const val DEFAULT_PICTURE = "DEFAULT_PICTURE"
        private const val DEFAULT_CURRENTLY_EATING = false
        private val DEFAULT_EATING_PLACE_ID = null
        private const val DEFAULT_IS_ONLINE = false
        
    }
    
    @Before
    fun setup() {
        coEvery { firestoreDomainRepository.getAllFirestoreUsers() } returns provideFirestoreUserEntitiesAsFlow()
        coEvery { firebaseAuthDomainRepository.getCurrentAuthenticatedUser() } returns provideAuthenticatedUserEntity()
    }
    
    @Test
    fun `nominal case - get coworker entities`() = testCoroutineRule.runTest {
        useCase.invoke().test {
            val result = awaitItem()
            awaitComplete()
            
            assertThat(result).isEqualTo(provideCoworkerEntities())
            
            coVerify {
                firestoreDomainRepository.getAllFirestoreUsers()
                firebaseAuthDomainRepository.getCurrentAuthenticatedUser()
            }
            
            confirmVerified(
                firebaseAuthDomainRepository,
                firestoreDomainRepository
            )
        }
    }
    
    
    //Region OUT
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
                    eatingPlaceId = DEFAULT_EATING_PLACE_ID,
                    online = DEFAULT_IS_ONLINE
                )
            )
        }
        emit(list.reversed())
    }
    
    private fun provideEatingFirestoreUserEntitiesAsFlow(): Flow<List<FirestoreUserEntity>> = flow {
        val list = mutableListOf<FirestoreUserEntity>()
        for (i in 3 downTo 0) {
            list.add(
                FirestoreUserEntity(
                    picture = DEFAULT_PICTURE,
                    displayName = DEFAULT_DISPLAY_NAME,
                    mailAddress = DEFAULT_MAIL,
                    uid = DEFAULT_UID,
                    currentlyEating = true,
                    eatingPlaceId = DEFAULT_EATING_PLACE_ID,
                    online = DEFAULT_IS_ONLINE
                )
            )
        }
        emit(list.reversed())
    }
    
    private fun provideCoworkerEntities() = List(4) {
        CoworkersEntity(
            uid = DEFAULT_UID,
            isEating = DEFAULT_CURRENTLY_EATING,
            eatingPlaceId = DEFAULT_EATING_PLACE_ID,
            pictureUrl = DEFAULT_PICTURE,
            name = DEFAULT_DISPLAY_NAME
        )
    }
    
    private fun provideEatingCoworkerEntities() = List(4) {
        CoworkersEntity(
            uid = DEFAULT_UID,
            isEating = true,
            eatingPlaceId = DEFAULT_EATING_PLACE_ID,
            pictureUrl = DEFAULT_PICTURE,
            name = DEFAULT_DISPLAY_NAME
        )
    }
    
    private fun provideAuthenticatedUserEntity() = AuthenticateUserEntity(
        picture = DEFAULT_PICTURE,
        displayName = DEFAULT_DISPLAY_NAME,
        mailAddress = DEFAULT_MAIL,
        uid = DEFAULT_OTHER_UID,
        currentlyEating = DEFAULT_CURRENTLY_EATING,
        eatingPlaceId = DEFAULT_EATING_PLACE_ID
    )
    
    //End region out
}