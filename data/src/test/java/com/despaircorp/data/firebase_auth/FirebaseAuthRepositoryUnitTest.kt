package com.despaircorp.data.firebase_auth

import android.net.Uri
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.despaircorp.data.utils.TestCoroutineRule
import com.despaircorp.domain.firebase_auth.model.AuthenticateUserEntity
import com.facebook.AccessToken
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FirebaseAuthRepositoryUnitTest {
    
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    companion object {
        private const val DEFAULT_MAIL = "DEFAULT_MAIL"
        private const val DEFAULT_PASSWORD = "DEFAULT_PASSWORD"
        private const val DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME"
        private const val DEFAULT_UID = "DEFAULT_UID"
        private val DEFAULT_PICTURE = mockk<Uri>()
        private const val DEFAULT_TOKEN = "DEFAULT_TOKEN"
        private const val DEFAULT_CURRENTLY_EATING = false
        private val DEFAULT_EATING_PLACE_IDE = null
    }
    
    private val firebaseAuth: FirebaseAuth = mockk()
    
    private val repository = FirebaseAuthDataRepository(
        coroutineDispatcherProvider = testCoroutineRule.getTestCoroutineDispatcherProvider(),
        firebaseAuth = firebaseAuth
    )
    
    @Before
    fun setup() {
        coEvery { firebaseAuth.currentUser } returns mockk()
        coEvery { DEFAULT_PICTURE.path } returns "DEFAULT_PICTURE"
    }
    
    
    @Test
    fun `nominal case - user already auth`() = testCoroutineRule.runTest {
        val result = repository.isUserAlreadyAuth()
        
        assertThat(result).isTrue()
        
        coVerify {
            firebaseAuth.currentUser
        }
        
        confirmVerified(firebaseAuth)
    }
    
    @Test
    fun `edge case - user not auth`() = testCoroutineRule.runTest {
        coEvery { firebaseAuth.currentUser } returns null
        
        val result = repository.isUserAlreadyAuth()
        
        assertThat(result).isFalse()
        
        coVerify {
            firebaseAuth.currentUser
        }
        
        confirmVerified(firebaseAuth)
    }
    
    @Test
    fun `nominal case - user with credentials exist`() = testCoroutineRule.runTest {
        val task: Task<AuthResult> = mockk()
        val slot = slot<OnCompleteListener<AuthResult>>()
        
        every { task.isSuccessful } returns true
        every { task.exception } returns null
        
        every {
            firebaseAuth.signInWithEmailAndPassword(DEFAULT_MAIL, DEFAULT_PASSWORD)
                .addOnCompleteListener(capture(slot))
        } answers {
            slot.captured.onComplete(task)
            task
        }
        
        val result = repository.isUserWithCredentialsExist(DEFAULT_MAIL, DEFAULT_PASSWORD)
        
        assertThat(result).isTrue()
        
        coVerify {
            firebaseAuth.signInWithEmailAndPassword(DEFAULT_MAIL, DEFAULT_PASSWORD)
                .addOnCompleteListener(any())
        }
        
        confirmVerified(firebaseAuth)
    }
    
    @Test
    fun `nominal case - user with credentials did not exist`() = testCoroutineRule.runTest {
        val task: Task<AuthResult> = mockk()
        val slot = slot<OnCompleteListener<AuthResult>>()
        
        every { task.isSuccessful } returns true
        every { task.exception } returns Exception()
        
        every {
            firebaseAuth.signInWithEmailAndPassword(DEFAULT_MAIL, DEFAULT_PASSWORD)
                .addOnCompleteListener(capture(slot))
        } answers {
            slot.captured.onComplete(task)
            task
        }
        
        val result = repository.isUserWithCredentialsExist(DEFAULT_MAIL, DEFAULT_PASSWORD)
        
        assertThat(result).isFalse()
        
        coVerify {
            firebaseAuth.signInWithEmailAndPassword(DEFAULT_MAIL, DEFAULT_PASSWORD)
                .addOnCompleteListener(any())
        }
        
        confirmVerified(firebaseAuth)
    }
    
    @Test
    fun `nominal case - create user with credentials success`() = testCoroutineRule.runTest {
        val task: Task<AuthResult> = mockk()
        val slot = slot<OnCompleteListener<AuthResult>>()
        
        every { task.isSuccessful } returns true
        every { task.exception } returns null
        
        every {
            firebaseAuth.createUserWithEmailAndPassword(DEFAULT_MAIL, DEFAULT_PASSWORD)
                .addOnCompleteListener(capture(slot))
        } answers {
            slot.captured.onComplete(task)
            task
        }
        
        val result = repository.createUserWithCredentials(DEFAULT_MAIL, DEFAULT_PASSWORD)
        
        assertThat(result).isTrue()
        
        coVerify {
            firebaseAuth.createUserWithEmailAndPassword(DEFAULT_MAIL, DEFAULT_PASSWORD)
                .addOnCompleteListener(any())
        }
        
        confirmVerified(firebaseAuth)
    }
    
    @Test
    fun `nominal case - create user with credentials failure`() = testCoroutineRule.runTest {
        val task: Task<AuthResult> = mockk()
        val slot = slot<OnCompleteListener<AuthResult>>()
        
        every { task.isSuccessful } returns false
        every { task.exception } returns null
        
        every {
            firebaseAuth.createUserWithEmailAndPassword(DEFAULT_MAIL, DEFAULT_PASSWORD)
                .addOnCompleteListener(capture(slot))
        } answers {
            slot.captured.onComplete(task)
            task
        }
        
        val result = repository.createUserWithCredentials(DEFAULT_MAIL, DEFAULT_PASSWORD)
        
        assertThat(result).isFalse()
        
        coVerify {
            firebaseAuth.createUserWithEmailAndPassword(DEFAULT_MAIL, DEFAULT_PASSWORD)
                .addOnCompleteListener(any())
        }
        
        confirmVerified(firebaseAuth)
    }
    
    @Test
    fun `error case - create user with credentials exception`() = testCoroutineRule.runTest {
        val task: Task<AuthResult> = mockk()
        val slot = slot<OnCompleteListener<AuthResult>>()
        
        every { task.isSuccessful } returns true
        every { task.exception } returns Exception()
        
        every {
            firebaseAuth.createUserWithEmailAndPassword(DEFAULT_MAIL, DEFAULT_PASSWORD)
                .addOnCompleteListener(capture(slot))
        } answers {
            slot.captured.onComplete(task)
            task
        }
        
        val result = repository.createUserWithCredentials(DEFAULT_MAIL, DEFAULT_PASSWORD)
        
        assertThat(result).isFalse()
        
        coVerify {
            firebaseAuth.createUserWithEmailAndPassword(DEFAULT_MAIL, DEFAULT_PASSWORD)
                .addOnCompleteListener(any())
        }
        
        confirmVerified(firebaseAuth)
    }
    
    @Test
    fun `nominal case - get authenticated user`() = testCoroutineRule.runTest {
        coEvery { firebaseAuth.currentUser?.displayName } returns DEFAULT_DISPLAY_NAME
        coEvery { firebaseAuth.currentUser?.uid } returns DEFAULT_UID
        coEvery { firebaseAuth.currentUser?.email } returns DEFAULT_MAIL
        coEvery { firebaseAuth.currentUser?.photoUrl } returns DEFAULT_PICTURE
        
        val result = repository.getCurrentAuthenticatedUser()
        
        assertThat(result).isEqualTo(
            AuthenticateUserEntity(
                picture = "DEFAULT_PICTURE",
                displayName = DEFAULT_DISPLAY_NAME,
                mailAddress = DEFAULT_MAIL,
                uid = DEFAULT_UID,
                currentlyEating = DEFAULT_CURRENTLY_EATING,
                eatingPlaceId = DEFAULT_EATING_PLACE_IDE
            )
        )
        
        coVerify {
            firebaseAuth.currentUser?.displayName
            firebaseAuth.currentUser?.uid
            firebaseAuth.currentUser?.email
            firebaseAuth.currentUser?.photoUrl
        }
        confirmVerified(firebaseAuth)
    }
    
    @Test
    fun `nominal case - get authenticated user with uri and display name null`() =
        testCoroutineRule.runTest {
            
            coEvery { firebaseAuth.currentUser?.displayName } returns null
            coEvery { firebaseAuth.currentUser?.uid } returns DEFAULT_UID
            coEvery { firebaseAuth.currentUser?.email } returns DEFAULT_MAIL
            coEvery { firebaseAuth.currentUser?.photoUrl } returns null
            
            val result = repository.getCurrentAuthenticatedUser()
            
            assertThat(result).isEqualTo(
                AuthenticateUserEntity(
                    picture = "https://w0.peakpx.com/wallpaper/733/998/HD-wallpaper-hedgedog-on-cloth-in-blur-green-bokeh-background-animals.jpg",
                    displayName = "none",
                    mailAddress = DEFAULT_MAIL,
                    uid = DEFAULT_UID,
                    currentlyEating = DEFAULT_CURRENTLY_EATING,
                    eatingPlaceId = DEFAULT_EATING_PLACE_IDE
                )
            )
            
            coVerify {
                firebaseAuth.currentUser?.displayName
                firebaseAuth.currentUser?.uid
                firebaseAuth.currentUser?.email
                firebaseAuth.currentUser?.photoUrl
            }
            confirmVerified(firebaseAuth)
        }
    
    @Test
    fun `nominal case - signin with token`() = testCoroutineRule.runTest {
        val task = mockk<Task<AuthResult>>()
        val slot = slot<OnCompleteListener<AuthResult>>()
        
        every { task.isSuccessful } returns true
        every { task.exception } returns null
        
        every {
            firebaseAuth.signInWithCredential(any()).addOnCompleteListener(capture(slot))
        } answers {
            slot.captured.onComplete(task)
            task
        }
        
        val accessToken = mockk<AccessToken> {
            every { token } returns DEFAULT_TOKEN
        }
        
        val result = repository.signInTokenUser(accessToken)
        
        assertThat(result).isTrue()
        
        coVerify {
            firebaseAuth.signInWithCredential(any()).addOnCompleteListener(any())
        }
        
        confirmVerified(firebaseAuth)
    }
    
    @Test
    fun `nominal case - signin with token with failure`() = testCoroutineRule.runTest {
        val task = mockk<Task<AuthResult>>()
        val slot = slot<OnCompleteListener<AuthResult>>()
        
        every { task.isSuccessful } returns true
        every { task.exception } returns Exception()
        
        every {
            firebaseAuth.signInWithCredential(any()).addOnCompleteListener(capture(slot))
        } answers {
            slot.captured.onComplete(task)
            task
        }
        
        val accessToken = mockk<AccessToken> {
            every { token } returns DEFAULT_TOKEN
        }
        
        val result = repository.signInTokenUser(accessToken)
        
        assertThat(result).isFalse()
        
        coVerify {
            firebaseAuth.signInWithCredential(any()).addOnCompleteListener(any())
        }
        
        confirmVerified(firebaseAuth)
    }
    
    @Test
    fun `nominal case - disconnect user success`() = testCoroutineRule.runTest {
        coJustRun { firebaseAuth.signOut() }
        
        val result = repository.disconnectUser()
        
        assertThat(result).isTrue()
        
        coVerify {
            firebaseAuth.signOut()
        }
        confirmVerified(firebaseAuth)
    }
    
    @Test
    fun `nominal case - disconnect user failure`() = testCoroutineRule.runTest {
        coEvery { firebaseAuth.signOut() } throws Exception()
        
        val result = repository.disconnectUser()
        
        assertThat(result).isFalse()
        
        coVerify {
            firebaseAuth.signOut()
        }
        confirmVerified(firebaseAuth)
    }
    
    @Test
    fun `nominal case - update email address success`() = testCoroutineRule.runTest {
        val task = mockk<Task<Void>>()
        val slot = slot<OnCompleteListener<Void>>()
        
        every { task.isSuccessful } returns true
        every { task.exception } returns null
        
        every {
            firebaseAuth.currentUser?.updateEmail(DEFAULT_MAIL)
                ?.addOnCompleteListener(capture(slot))
        } answers {
            slot.captured.onComplete(task)
            task
        }
        
        val result = repository.updateMailAddress(DEFAULT_MAIL)
        
        assertThat(result).isTrue()
        
        coVerify {
            firebaseAuth.currentUser?.updateEmail(DEFAULT_MAIL)?.addOnCompleteListener(any())
        }
        
        confirmVerified(firebaseAuth)
    }
    
    @Test
    fun `nominal case - update email address failure`() = testCoroutineRule.runTest {
        val task = mockk<Task<Void>>()
        val slot = slot<OnCompleteListener<Void>>()
        
        every { task.isSuccessful } returns false
        every { task.exception } returns null
        
        every {
            firebaseAuth.currentUser?.updateEmail(DEFAULT_MAIL)
                ?.addOnCompleteListener(capture(slot))
        } answers {
            slot.captured.onComplete(task)
            task
        }
        
        val result = repository.updateMailAddress(DEFAULT_MAIL)
        
        assertThat(result).isFalse()
        
        coVerify {
            firebaseAuth.currentUser?.updateEmail(DEFAULT_MAIL)?.addOnCompleteListener(any())
        }
        
        confirmVerified(firebaseAuth)
    }
    
    @Test
    fun `nominal case - update email address exception`() = testCoroutineRule.runTest {
        val task = mockk<Task<Void>>()
        val slot = slot<OnCompleteListener<Void>>()
        
        every { task.isSuccessful } returns false
        every { task.exception } returns Exception()
        
        every {
            firebaseAuth.currentUser?.updateEmail(DEFAULT_MAIL)
                ?.addOnCompleteListener(capture(slot))
        } answers {
            slot.captured.onComplete(task)
            task
        }
        
        val result = repository.updateMailAddress(DEFAULT_MAIL)
        
        assertThat(result).isFalse()
        
        coVerify {
            firebaseAuth.currentUser?.updateEmail(DEFAULT_MAIL)?.addOnCompleteListener(any())
        }
        
        confirmVerified(firebaseAuth)
    }
    
    @Test
    fun `error case - update email address with null user`() = testCoroutineRule.runTest {
        every { firebaseAuth.currentUser } returns null
        
        val result = repository.updateMailAddress(DEFAULT_MAIL)
        
        assertThat(result).isFalse()
        
        coVerify {
            firebaseAuth.currentUser
        }
        
        confirmVerified(firebaseAuth)
    }
    
    @Test
    fun `nominal case - update password success`() = testCoroutineRule.runTest {
        val task = mockk<Task<Void>>()
        val slot = slot<OnCompleteListener<Void>>()
        
        every { task.isSuccessful } returns true
        every { task.exception } returns null
        
        every {
            firebaseAuth.currentUser?.updatePassword(DEFAULT_PASSWORD)
                ?.addOnCompleteListener(capture(slot))
        } answers {
            slot.captured.onComplete(task)
            task
        }
        
        val result = repository.updatePassword(DEFAULT_PASSWORD)
        
        assertThat(result).isTrue()
        
        coVerify {
            firebaseAuth.currentUser?.updatePassword(DEFAULT_PASSWORD)?.addOnCompleteListener(any())
        }
        
        confirmVerified(firebaseAuth)
    }
    
    @Test
    fun `nominal case - update password failure`() = testCoroutineRule.runTest {
        val task = mockk<Task<Void>>()
        val slot = slot<OnCompleteListener<Void>>()
        
        every { task.isSuccessful } returns false
        every { task.exception } returns null
        
        every {
            firebaseAuth.currentUser?.updatePassword(DEFAULT_PASSWORD)
                ?.addOnCompleteListener(capture(slot))
        } answers {
            slot.captured.onComplete(task)
            task
        }
        
        val result = repository.updatePassword(DEFAULT_PASSWORD)
        
        assertThat(result).isFalse()
        
        coVerify {
            firebaseAuth.currentUser?.updatePassword(DEFAULT_PASSWORD)?.addOnCompleteListener(any())
        }
        
        confirmVerified(firebaseAuth)
    }
    
    @Test
    fun `nominal case - update password exception`() = testCoroutineRule.runTest {
        val task = mockk<Task<Void>>()
        val slot = slot<OnCompleteListener<Void>>()
        
        every { task.isSuccessful } returns false
        every { task.exception } returns Exception()
        
        every {
            firebaseAuth.currentUser?.updatePassword(DEFAULT_PASSWORD)
                ?.addOnCompleteListener(capture(slot))
        } answers {
            slot.captured.onComplete(task)
            task
        }
        
        val result = repository.updatePassword(DEFAULT_PASSWORD)
        
        assertThat(result).isFalse()
        
        coVerify {
            firebaseAuth.currentUser?.updatePassword(DEFAULT_PASSWORD)?.addOnCompleteListener(any())
        }
        
        confirmVerified(firebaseAuth)
    }
    
    @Test
    fun `nominal case - update password null user`() = testCoroutineRule.runTest {
        every { firebaseAuth.currentUser } returns null
        
        val result = repository.updatePassword(DEFAULT_PASSWORD)
        
        assertThat(result).isFalse()
        
        coVerify {
            firebaseAuth.currentUser
        }
        
        confirmVerified(firebaseAuth)
    }
}