package com.despaircorp.domain.firestore

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.firebase_auth.model.AuthenticateUserEntity
import com.despaircorp.domain.firestore.model.CoworkersChatEntity
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

class GetAllCoworkersForChatUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val firestoreDomainRepository: FirestoreDomainRepository = mockk()
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository = mockk()
    
    private val useCase = GetAllCoworkersForChatUseCase(
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
        coEvery { firestoreDomainRepository.getAllFirestoreUsersAsFlow() } returns provideFirestoreUserEntitiesAsFlow()
        coEvery { firebaseAuthDomainRepository.getCurrentAuthenticatedUser() } returns provideAuthenticatedUserEntity()
        
    }
    
    @Test
    fun `nominal case - get all coworker chat entities`() = testCoroutineRule.runTest {
        useCase.invoke().test {
            val result = awaitItem()
            awaitComplete()
            
            assertThat(result).isEqualTo(provideCoworkersChatEntities())
            
            coVerify {
                firestoreDomainRepository.getAllFirestoreUsersAsFlow()
                firebaseAuthDomainRepository.getCurrentAuthenticatedUser()
            }
            
            confirmVerified(
                firestoreDomainRepository,
                firebaseAuthDomainRepository
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
    
    private fun provideCoworkersChatEntities() = List(4) {
        CoworkersChatEntity(
            uid = DEFAULT_UID,
            pictureUrl = DEFAULT_PICTURE,
            name = DEFAULT_DISPLAY_NAME,
            isOnline = DEFAULT_IS_ONLINE
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








