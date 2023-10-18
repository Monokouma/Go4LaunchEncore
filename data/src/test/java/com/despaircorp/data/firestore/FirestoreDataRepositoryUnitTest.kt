package com.despaircorp.data.firestore

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.despaircorp.data.firestore.dto.FirestoreUserDto
import com.despaircorp.data.utils.TestCoroutineRule
import com.despaircorp.domain.firebaseAuth.model.AuthenticateUserEntity
import com.despaircorp.domain.firestore.model.FirestoreUserEntity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FirestoreDataRepositoryUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    companion object {
        private const val DEFAULT_UID = "DEFAULT_UID"
        private const val DEFAULT_MAIL = "DEFAULT_MAIL"
        private const val DEFAULT_PASSWORD = "DEFAULT_PASSWORD"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
        private const val DEFAULT_PICTURE = "DEFAULT_PICTURE"
        private const val DEFAULT_TOKEN = "DEFAULT_TOKEN"
    }
    
    private val firestore: FirebaseFirestore = mockk()
    
    private val repository = FirestoreDataRepository(
        coroutineDispatcherProvider = testCoroutineRule.getTestCoroutineDispatcherProvider(),
        firestore = firestore
    )
    
    @Test
    fun `nominal case - insert user success`() = testCoroutineRule.runTest {
        val task: Task<Void> = mockk()
        val slot = slot<OnCompleteListener<Void>>()
        
        every { task.isSuccessful } returns true
        every { task.exception } returns null
        
        every {
            firestore
                .collection("users")
                .document(DEFAULT_UID)
                .set(provideAuthenticatedUserEntity())
                .addOnCompleteListener(capture(slot))
        } answers {
            slot.captured.onComplete(task)
            task
        }
        
        val result = repository.insertUser(provideAuthenticatedUserEntity())
        
        assertThat(result).isTrue()
        
        coVerify {
            firestore
                .collection("users")
                .document(DEFAULT_UID)
                .set(provideAuthenticatedUserEntity())
                .addOnCompleteListener(any())
        }
        
        confirmVerified(firestore)
    }
    
    @Test
    fun `edge case - insert user failure`() = testCoroutineRule.runTest {
        val task: Task<Void> = mockk()
        val slot = slot<OnCompleteListener<Void>>()
        
        every { task.isSuccessful } returns true
        every { task.exception } returns Exception()
        
        every {
            firestore
                .collection("users")
                .document(DEFAULT_UID)
                .set(provideAuthenticatedUserEntity())
                .addOnCompleteListener(capture(slot))
        } answers {
            slot.captured.onComplete(task)
            task
        }
        
        val result = repository.insertUser(provideAuthenticatedUserEntity())
        
        assertThat(result).isFalse()
        
        coVerify {
            firestore
                .collection("users")
                .document(DEFAULT_UID)
                .set(provideAuthenticatedUserEntity())
                .addOnCompleteListener(any())
        }
        
        confirmVerified(firestore)
    }
    
    @Test
    fun `nominal case - is user exist true`() = testCoroutineRule.runTest {
        val task: Task<DocumentSnapshot> = mockk()
        val slot = slot<OnCompleteListener<DocumentSnapshot>>()
        
        every { task.isSuccessful } returns true
        every { task.exception } returns null
        every { task.result.exists() } returns true
        every {
            firestore.collection("users").document(DEFAULT_UID).get()
                .addOnCompleteListener(capture(slot))
        } answers {
            slot.captured.onComplete(task)
            task
        }
        
        val result = repository.isUserExist(DEFAULT_UID)
        
        assertThat(result).isTrue()
        
        coVerify {
            firestore.collection("users").document(DEFAULT_UID).get()
                .addOnCompleteListener(any())
        }
        
        confirmVerified(firestore)
    }
    
    @Test
    fun `nominal case - is user exist false`() = testCoroutineRule.runTest {
        val task: Task<DocumentSnapshot> = mockk()
        val slot = slot<OnCompleteListener<DocumentSnapshot>>()
        
        every { task.isSuccessful } returns true
        every { task.exception } returns null
        every { task.result.exists() } returns false
        every {
            firestore.collection("users").document(DEFAULT_UID).get()
                .addOnCompleteListener(capture(slot))
        } answers {
            slot.captured.onComplete(task)
            task
        }
        
        val result = repository.isUserExist(DEFAULT_UID)
        
        assertThat(result).isFalse()
        
        coVerify {
            firestore.collection("users").document(DEFAULT_UID).get()
                .addOnCompleteListener(any())
        }
        
        confirmVerified(firestore)
    }
    
    @Test
    fun `nominal case - is user exist exception`() = testCoroutineRule.runTest {
        val task: Task<DocumentSnapshot> = mockk()
        val slot = slot<OnCompleteListener<DocumentSnapshot>>()
        
        every { task.isSuccessful } returns true
        every { task.exception } returns Exception()
        every { task.result.exists() } returns true
        every {
            firestore.collection("users").document(DEFAULT_UID).get()
                .addOnCompleteListener(capture(slot))
        } answers {
            slot.captured.onComplete(task)
            task
        }
        
        val result = repository.isUserExist(DEFAULT_UID)
        
        assertThat(result).isFalse()
        
        coVerify {
            firestore.collection("users").document(DEFAULT_UID).get()
                .addOnCompleteListener(any())
        }
        
        confirmVerified(firestore)
    }
    
    @Test
    fun `nominal case - get user success`() = testCoroutineRule.runTest {
        val slot = slot<EventListener<DocumentSnapshot>>()
        every {
            firestore.collection("users").document(DEFAULT_UID).addSnapshotListener(capture(slot))
        } returns mockk()
        
        var triggered = false
        val job = launch {
            // When
            val result = repository.getUser(DEFAULT_UID)
            triggered = true
            
            // Then
            assertThat(result).isEqualTo(provideFirestoreUserEntity())
        }
        
        runCurrent()
        slot.captured.onEvent(
            mockk {
                every { toObject(FirestoreUserDto::class.java) } returns provideFirestoreUserDto()
            },
            null
        )
        job.join()
        assertTrue(triggered)
    }
    
    @Test
    fun `nominal case - get user with display name and picture null`() = testCoroutineRule.runTest {
        val slot = slot<EventListener<DocumentSnapshot>>()
        every {
            firestore.collection("users").document(DEFAULT_UID).addSnapshotListener(capture(slot))
        } returns mockk()
        
        var triggered = false
        val job = launch {
            // When
            val result = repository.getUser(DEFAULT_UID)
            triggered = true
            
            // Then
            assertThat(result).isEqualTo(
                FirestoreUserEntity(
                    picture = "https://w0.peakpx.com/wallpaper/733/998/HD-wallpaper-hedgedog-on-cloth-in-blur-green-bokeh-background-animals.jpg",
                    displayName = "none",
                    mailAddress = DEFAULT_MAIL,
                    uid = DEFAULT_UID
                )
            )
        }
        
        runCurrent()
        slot.captured.onEvent(
            mockk {
                every { toObject(FirestoreUserDto::class.java) } returns FirestoreUserDto(
                    displayName = null,
                    mailAddress = DEFAULT_MAIL,
                    picture = null,
                    uid = DEFAULT_UID
                )
            },
            null
        )
        job.join()
        assertTrue(triggered)
    }
    
    @Test
    fun `nominal test - update user name success`() = testCoroutineRule.runTest {
        coEvery {
            firestore
                .collection("users")
                .document(DEFAULT_UID)
                .update("displayName", DEFAULT_DISPLAY_NAME)
        } returns getDefaultSetUserTask()
        
        val result = repository.updateUsername(DEFAULT_DISPLAY_NAME, DEFAULT_UID)
        
        assertThat(result).isTrue()
        
        coVerify {
            firestore
                .collection("users")
                .document(DEFAULT_UID)
                .update("displayName", DEFAULT_DISPLAY_NAME)
        }
        
        confirmVerified(firestore)
    }
    
    @Test
    fun `nominal test - update user name failure`() = testCoroutineRule.runTest {
        coEvery {
            firestore
                .collection("users")
                .document(DEFAULT_UID)
                .update("displayName", DEFAULT_DISPLAY_NAME)
        } returns getDefaultSetUserTaskWithException()
        
        val result = repository.updateUsername(DEFAULT_DISPLAY_NAME, DEFAULT_UID)
        
        assertThat(result).isFalse()
        
        coVerify {
            firestore
                .collection("users")
                .document(DEFAULT_UID)
                .update("displayName", DEFAULT_DISPLAY_NAME)
        }
        
        confirmVerified(firestore)
    }
    
    //Region Out
    private inline fun getDefaultSetUserTask(crossinline mockkBlock: Task<Void>.() -> Unit = {}): Task<Void> =
        mockk {
            every { isComplete } returns true
            every { exception } returns null
            every { isCanceled } returns false
            every { result } returns mockk()
            
            mockkBlock(this)
        }
    
    private inline fun getDefaultSetUserTaskWithException(crossinline mockkBlock: Task<Void>.() -> Unit = {}): Task<Void> =
        mockk {
            every { isComplete } returns true
            every { exception } returns Exception()
            every { isCanceled } returns false
            every { result } returns mockk()
            
            mockkBlock(this)
        }
    
    private fun provideAuthenticatedUserEntity() = AuthenticateUserEntity(
        picture = DEFAULT_PICTURE,
        displayName = DEFAULT_DISPLAY_NAME,
        mailAddress = DEFAULT_MAIL,
        uid = DEFAULT_UID
    )
    
    private fun provideFirestoreUserDto() = FirestoreUserDto(
        picture = DEFAULT_PICTURE,
        displayName = DEFAULT_DISPLAY_NAME,
        mailAddress = DEFAULT_MAIL,
        uid = DEFAULT_UID
    )
    
    private fun provideFirestoreUserEntity() = FirestoreUserEntity(
        picture = DEFAULT_PICTURE,
        displayName = DEFAULT_DISPLAY_NAME,
        mailAddress = DEFAULT_MAIL,
        uid = DEFAULT_UID
    )
    //End region out
}