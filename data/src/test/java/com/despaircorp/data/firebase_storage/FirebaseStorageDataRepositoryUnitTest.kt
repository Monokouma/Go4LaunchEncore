package com.despaircorp.data.firebase_storage

import android.net.Uri
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.despaircorp.data.utils.TestCoroutineRule
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
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
    
    @Test
    fun `nominal case - update user image success`() = testCoroutineRule.runTest {
        val uploadTask: UploadTask = mockk()
        val uploadSlot = slot<OnCompleteListener<UploadTask.TaskSnapshot>>()
        
        every { uploadTask.isComplete } returns true
        every { uploadTask.isSuccessful } returns true
        every { uploadTask.isCanceled } returns false
        every { uploadTask.exception } returns null
        every { uploadTask.result } returns mockk()
        
        every {
            firebaseStorage.reference.child(DEFAULT_UID).putFile(DEFAULT_PICTURE_URI)
        } answers {
            every { uploadTask.addOnCompleteListener(capture(uploadSlot)) } answers {
                uploadSlot.captured.onComplete(uploadTask)
                uploadTask
            }
            uploadTask
        }
        
        val downloadTask: Task<Uri> = mockk()
        val downloadSlot = slot<OnCompleteListener<Uri>>()
        
        every { downloadTask.isComplete } returns true
        every { downloadTask.isSuccessful } returns true
        every { downloadTask.isCanceled } returns false
        every { downloadTask.exception } returns null
        every { downloadTask.result } returns DEFAULT_PICTURE_URI
        
        every {
            firebaseStorage.reference.child(DEFAULT_UID).downloadUrl
        } answers {
            every { downloadTask.addOnCompleteListener(capture(downloadSlot)) } answers {
                downloadSlot.captured.onComplete(downloadTask)
                downloadTask
            }
            downloadTask
        }
        
        val result = repository.updateUserImage(DEFAULT_UID, DEFAULT_PICTURE_URI)
        
        assertThat(result).isEqualTo("$DEFAULT_PICTURE_URI")
        
        coVerify {
            firebaseStorage.reference.child(DEFAULT_UID).putFile(DEFAULT_PICTURE_URI)
            firebaseStorage.reference.child(DEFAULT_UID).downloadUrl
        }
        
        confirmVerified(firebaseStorage)
    }
    
    @Test
    fun `nominal case - update user image failure`() = testCoroutineRule.runTest {
        val uploadTask: UploadTask = mockk()
        val uploadSlot = slot<OnCompleteListener<UploadTask.TaskSnapshot>>()
        
        every { uploadTask.isComplete } returns true
        every { uploadTask.isSuccessful } returns true
        every { uploadTask.isCanceled } returns false
        every { uploadTask.exception } returns null
        every { uploadTask.result } returns mockk()
        
        every {
            firebaseStorage.reference.child(DEFAULT_UID).putFile(DEFAULT_PICTURE_URI)
        } answers {
            every { uploadTask.addOnCompleteListener(capture(uploadSlot)) } answers {
                uploadSlot.captured.onComplete(uploadTask)
                uploadTask
            }
            uploadTask
        }
        
        val downloadTask: Task<Uri> = mockk()
        val downloadSlot = slot<OnCompleteListener<Uri>>()
        
        every { downloadTask.isComplete } returns true
        every { downloadTask.isSuccessful } returns true
        every { downloadTask.isCanceled } returns false
        every { downloadTask.exception } returns null
        every { downloadTask.result } returns null
        
        every {
            firebaseStorage.reference.child(DEFAULT_UID).downloadUrl
        } answers {
            every { downloadTask.addOnCompleteListener(capture(downloadSlot)) } answers {
                downloadSlot.captured.onComplete(downloadTask)
                downloadTask
            }
            downloadTask
        }
        
        val result = repository.updateUserImage(DEFAULT_UID, DEFAULT_PICTURE_URI)
        
        assertThat(result).isNull()
        
        coVerify {
            firebaseStorage.reference.child(DEFAULT_UID).putFile(DEFAULT_PICTURE_URI)
            firebaseStorage.reference.child(DEFAULT_UID).downloadUrl
        }
        
        confirmVerified(firebaseStorage)
    }
}