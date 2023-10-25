package com.despaircorp.domain.firebaseStorage

import android.net.Uri
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import com.despaircorp.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UpdateUserImageThenGetLinkUseCaseUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val firebaseStorageDomainRepository: FirebaseStorageDomainRepository = mockk()
    
    private val useCase = UpdateUserImageThenGetLinkUseCase(
        firebaseStorageDomainRepository
    )
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private val DEFAULT_PICTURE = mockk<Uri>()
        private const val DEFAULT_PICTURE_URL = "DEFAULT_PICTURE_URL"
        
    }
    
    @Before
    fun setup() {
        coEvery {
            firebaseStorageDomainRepository.updateUserImage(
                DEFAULT_UID,
                DEFAULT_PICTURE
            )
        } returns DEFAULT_PICTURE_URL
    }
    
    @Test
    fun `nominal case - success`() = testCoroutineRule.runTest {
        val result = useCase.invoke(DEFAULT_UID, DEFAULT_PICTURE)
        
        assertThat(result).isEqualTo(DEFAULT_PICTURE_URL)
        
        coVerify {
            firebaseStorageDomainRepository.updateUserImage(DEFAULT_UID, DEFAULT_PICTURE)
        }
        
        confirmVerified(firebaseStorageDomainRepository)
    }
    
    @Test
    fun `nominal case - failure`() = testCoroutineRule.runTest {
        coEvery {
            firebaseStorageDomainRepository.updateUserImage(
                DEFAULT_UID,
                DEFAULT_PICTURE
            )
        } returns "error"
        
        val result = useCase.invoke(DEFAULT_UID, DEFAULT_PICTURE)
        
        assertThat(result).isNotEqualTo(DEFAULT_PICTURE_URL)
        
        coVerify {
            firebaseStorageDomainRepository.updateUserImage(DEFAULT_UID, DEFAULT_PICTURE)
        }
        
        confirmVerified(firebaseStorageDomainRepository)
    }
}