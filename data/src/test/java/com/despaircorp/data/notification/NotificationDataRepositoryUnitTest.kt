package com.despaircorp.data.notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationManagerCompat
import com.despaircorp.data.R
import com.despaircorp.data.utils.TestCoroutineRule
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
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
        private const val DEFAULT_USERNAME = "DEFAULT_USERNAME"
        private const val DEFAULT_UID = "DEFAULT_UID"
    }

    @Before
    fun setup() {
        repository = NotificationDataRepository(
            application = application,
            notificationManagerCompat = notificationManagerCompat,
        )
    }

    @Test
    fun `nominal case - create channel`() = testCoroutineRule.runTest {
        justRun {
            notificationManagerCompat.createNotificationChannel(any<NotificationChannel>())
        }

        repository.createChannel()

        verify {
            notificationManagerCompat.createNotificationChannel(
                NotificationChannel("Go4Lunch", "Go 4 Lunch !", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = ""
                }
            )
        }

        confirmVerified(notificationManagerCompat)
    }

    @Test
    fun `nominal case - notify`() = testCoroutineRule.runTest {
        justRun { notificationManagerCompat.notify(0, any()) }

        repository.notify(DEFAULT_USERNAME, DEFAULT_UID)

        coVerify {
            notificationManagerCompat.notify(0, match { notification ->
                notification.smallIcon.resId == R.drawable.lunchbox
            })
        }

        confirmVerified(notificationManagerCompat)
    }

}