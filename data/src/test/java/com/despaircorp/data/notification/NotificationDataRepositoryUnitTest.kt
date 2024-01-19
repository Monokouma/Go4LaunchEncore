package com.despaircorp.data.notification

import android.app.Application
import android.app.NotificationChannel
import androidx.core.app.NotificationManagerCompat
import com.despaircorp.data.R
import com.despaircorp.data.utils.TestCoroutineRule
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
    private val notificationManagerCompat: NotificationManagerCompat = mockk()
    
    private lateinit var repository: NotificationDataRepository
    
    companion object {
        private const val DEFAULT_APP_NAME = "DEFAULT_APP_NAME"
        private const val DEFAULT_HELLO = "DEFAULT_HELLO"
    }
    
    @Before
    fun setup() {
        repository = NotificationDataRepository(
            application = application,
            notificationManagerCompat = notificationManagerCompat,
        )
        every { application.getString(R.string.app_name) } returns DEFAULT_APP_NAME
        every { application.getString(R.string.hello) } returns DEFAULT_HELLO
    }
    
    @Test
    fun `nominal case - create channel`() = testCoroutineRule.runTest {
        justRun {
            notificationManagerCompat.createNotificationChannel(any<NotificationChannel>())
        }
        repository.createChannel()
    }
    
}