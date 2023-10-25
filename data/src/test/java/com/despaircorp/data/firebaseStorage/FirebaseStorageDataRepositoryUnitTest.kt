package com.despaircorp.data.firebaseStorage

import android.net.Uri
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.despaircorp.data.utils.TestCoroutineRule
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class FirebaseStorageDataRepositoryUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private val DEFAULT_PICTURE = mockk<Uri>()
    }
    
    private val firebaseStorage: FirebaseStorage = mockk()
    
    private val repository = FirebaseStorageDataRepository(
        coroutineDispatcherProvider = testCoroutineRule.getTestCoroutineDispatcherProvider(),
        firebaseStorage = firebaseStorage
    )
    
    @Test
    fun `nominal case - update user image success`() = testCoroutineRule.runTest {
        val task = mockk<UploadTask>()
        val slot = slot<OnCompleteListener<UploadTask.TaskSnapshot>>()
        
        every { task.isSuccessful } returns true
        every { task.exception } returns null
        
        every {
            firebaseStorage.reference.child(DEFAULT_UID).putFile(DEFAULT_PICTURE)
                .addOnCompleteListener(capture(slot))
        } answers {
            slot.captured.onComplete(task)
            task
        }
        
        
        val uploadSlot = slot<OnSuccessListener<Uri>>()
        val uploadTask = mockk<Task<Uri>>()
        val uri = mockk<Uri>()
        
        every { uploadTask.isSuccessful } returns true
        every { uri.toString() } returns DEFAULT_PICTURE.toString()
        
        every {
            firebaseStorage.reference.child(DEFAULT_UID).downloadUrl.addOnSuccessListener(
                capture(
                    uploadSlot
                )
            )
        } answers {
            uploadSlot.captured.onSuccess(uri)
            uploadTask
        }
        
        val result = repository.updateUserImage(DEFAULT_UID, DEFAULT_PICTURE)
        
        assertThat(result).isEqualTo(DEFAULT_PICTURE.toString())
        
        coVerify {
            firebaseStorage.reference.child(DEFAULT_UID).putFile(DEFAULT_PICTURE)
                .addOnCompleteListener(any())
            firebaseStorage.reference.child(DEFAULT_UID).downloadUrl.addOnSuccessListener(any())
        }
        
        confirmVerified(firebaseStorage)
    }
    
    @Ignore
    @Test
    fun `nominal case - update user image failure`() = testCoroutineRule.runTest {
        
        val task = mockk<UploadTask>()
        val slot = slot<OnCompleteListener<UploadTask.TaskSnapshot>>()
        
        every { task.isSuccessful } returns false
        every { task.exception } returns null
        
        every {
            firebaseStorage.reference.child(DEFAULT_UID).putFile(DEFAULT_PICTURE)
                .addOnCompleteListener(capture(slot))
        } answers {
            slot.captured.onComplete(task)
            task
        }
        
        
        val uploadSlot = slot<OnSuccessListener<Uri>>()
        val uploadTask = mockk<Task<Uri>>()
        val uri = mockk<Uri>()
        
        every { uploadTask.isSuccessful } returns true
        every { uri.toString() } returns DEFAULT_PICTURE.toString()
        
        every {
            firebaseStorage.reference.child(DEFAULT_UID).downloadUrl.addOnSuccessListener(
                capture(
                    uploadSlot
                )
            )
        } answers {
            uploadSlot.captured.onSuccess(uri)
            uploadTask
        }
        
        
        val result = repository.updateUserImage(DEFAULT_UID, DEFAULT_PICTURE)
        
        println(result)
        
        coVerify {
            firebaseStorage.reference.child(DEFAULT_UID).putFile(DEFAULT_PICTURE)
                .addOnCompleteListener(any())
            firebaseStorage.reference.child(DEFAULT_UID).downloadUrl.addOnSuccessListener(any())
        }
        
        confirmVerified(firebaseStorage)
    }
}