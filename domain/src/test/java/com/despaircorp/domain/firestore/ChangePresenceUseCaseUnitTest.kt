package com.despaircorp.domain.firestore

import com.despaircorp.domain.firebase_auth.FirebaseAuthDomainRepository
import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChangePresenceUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val firestoreDomainRepository: FirestoreDomainRepository = mockk()
    private val firebaseAuthDomainRepository: FirebaseAuthDomainRepository = mockk()
    
    private val useCase = ChangePresenceUseCase(
        firestoreDomainRepository = firestoreDomainRepository,
        firebaseAuthDomainRepository = firebaseAuthDomainRepository
    )
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_PRESENCE = true
    }
    
    @Before
    fun setup() {
        coEvery { firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid } returns DEFAULT_UID
        coJustRun { firestoreDomainRepository.updateUserPresence(DEFAULT_UID, DEFAULT_PRESENCE) }
    }
    
    @Test
    fun `nominal case - change presence true`() = testCoroutineRule.runTest {
        useCase.invoke(DEFAULT_PRESENCE)
        
        coVerify {
            firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid
            firestoreDomainRepository.updateUserPresence(DEFAULT_UID, DEFAULT_PRESENCE)
        }
        
        confirmVerified(
            firebaseAuthDomainRepository,
            firestoreDomainRepository
        )
    }
    
    @Test
    fun `nominal case - change presence false`() = testCoroutineRule.runTest {
        coJustRun { firestoreDomainRepository.updateUserPresence(DEFAULT_UID, false) }
        
        useCase.invoke(false)
        
        coVerify {
            firebaseAuthDomainRepository.getCurrentAuthenticatedUser().uid
            firestoreDomainRepository.updateUserPresence(DEFAULT_UID, false)
        }
        
        confirmVerified(
            firebaseAuthDomainRepository,
            firestoreDomainRepository
        )
    }
}