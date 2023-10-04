package com.despaircorp.data.firebaseAuth

import android.net.Uri
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.despaircorp.data.utils.TestCoroutineRule
import com.despaircorp.domain.firebaseAuth.model.AuthenticateUserEntity
import com.facebook.AccessToken
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
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
                uid = DEFAULT_UID
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
                    uid = DEFAULT_UID
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
        
        assertTrue(result)
    }
}