package com.despaircorp.data.notification

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.despaircorp.data.utils.TestCoroutineRule
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NotificationDataRepositoryUnitTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    
    private val application: Application = mockk()
    private val notificationManager: NotificationManager = mockk()
    private val context: Context = mockk()
    
    private lateinit var repository: NotificationDataRepository
    
    companion object {
        private const val DEFAULT_USERNAME = "DEFAULT_USERNAME"
        private const val DEFAULT_UID = "DEFAULT_UID"
    }
    
    @Before
    fun setup() {
        every { application.getSystemService(Context.NOTIFICATION_SERVICE) } returns notificationManager
        every { application.applicationContext } returns context
        
        repository = NotificationDataRepository(
            dispatcherProvider = testCoroutineRule.getTestCoroutineDispatcherProvider(),
            application = application
        )
    }
    
    @Test
    fun `nominal case - create channel`() = testCoroutineRule.runTest {
        justRun {
            notificationManager.createNotificationChannel(any())
        }
        
        val result = repository.createChannel()
        assertThat(result).isTrue()
        
        coVerify {
            notificationManager.createNotificationChannel(any())
        }
        
        confirmVerified(notificationManager)
    }
    
    @Test
    fun `error case - create channel`() = testCoroutineRule.runTest {
        justRun {
            notificationManager.createNotificationChannel(mockk())
        } andThenThrows Exception()
        
        val result = repository.createChannel()
        assertThat(result).isFalse()
        
        coVerify {
            notificationManager.createNotificationChannel(any())
        }
        
        confirmVerified(notificationManager)
    }
    
}