package com.despaircorp.data.firebaseStorage

import android.net.Uri
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.data.utils.TestCoroutineRule
import com.despaircorp.data.utils.safeAwait
import com.google.firebase.storage.FirebaseStorage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class FirebaseStorageDataRepositoryUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private val DEFAULT_PICTURE_URI = mockk<Uri>()
        private const val DEFAULT_PICURE_STRING = "DEFAULT_PICURE_STRING"
    }
    
    private val firebaseStorage: FirebaseStorage = mockk()
    
    private val repository = FirebaseStorageDataRepository(
        coroutineDispatcherProvider = testCoroutineRule.getTestCoroutineDispatcherProvider(),
        firebaseStorage = firebaseStorage
    )
    
    @Ignore
    @Test
    fun `nominal case - update user image success`() = testCoroutineRule.runTest {
        coEvery {
            firebaseStorage.reference.child(DEFAULT_UID).putFile(DEFAULT_PICTURE_URI).safeAwait()
        } returns mockk()
        coEvery {
            firebaseStorage.reference.child(DEFAULT_UID).downloadUrl.safeAwait()?.toString()
        } returns DEFAULT_PICURE_STRING
        
        val result = repository.updateUserImage(DEFAULT_UID, DEFAULT_PICTURE_URI)
        
        assertThat(result).isEqualTo(DEFAULT_PICURE_STRING)
        
        coVerify {
            firebaseStorage.reference.child(DEFAULT_UID).putFile(DEFAULT_PICTURE_URI).safeAwait()
            firebaseStorage.reference.child(DEFAULT_UID).downloadUrl.safeAwait()?.toString()
        }
        
        confirmVerified(firebaseStorage)
    }
    
    
}