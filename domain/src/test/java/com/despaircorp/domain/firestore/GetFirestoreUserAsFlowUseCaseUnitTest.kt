package com.despaircorp.domain.firestore

import app.cash.turbine.test
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

class GetFirestoreUserAsFlowUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_MAIL = "DEFAULT_MAIL"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
        private const val DEFAULT_PICTURE = "DEFAULT_PICTURE"
        
    }
    
    private val firestoreDomainRepository: FirestoreDomainRepository = mockk()
    
    private val useCase = GetFirestoreUserAsFlowUseCase(
        firestoreDomainRepository = firestoreDomainRepository
    )
    
    @Before
    fun setup() {
        coEvery { firestoreDomainRepository.getUserAsFlow(DEFAULT_UID) } returns provideFirestoreUserEntityAsFlow()
    }
    
    @Test
    fun `nominal case - get user`() = testCoroutineRule.runTest {
        useCase.invoke(DEFAULT_UID).test {
            
            awaitComplete()
            
            coVerify {
                firestoreDomainRepository.getUserAsFlow(DEFAULT_UID)
            }
            confirmVerified(firestoreDomainRepository)
        }
    }
    
    //Region Out
    private fun provideFirestoreUserEntityAsFlow(): Flow<FirestoreUserEntity> = flow {
        FirestoreUserEntity(
            picture = DEFAULT_PICTURE,
            displayName = DEFAULT_DISPLAY_NAME,
            mailAddress = DEFAULT_MAIL,
            uid = DEFAULT_UID
        )
    }
    
    //End region out
}