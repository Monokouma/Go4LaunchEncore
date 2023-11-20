package com.despaircorp.data.notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.despaircorp.data.R
import com.despaircorp.data.utils.CoroutineDispatcherProvider
import com.despaircorp.domain.notifications.NotificationDomainRepository
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotificationDataRepository @Inject constructor(
    private val dispatcherProvider: CoroutineDispatcherProvider,
    private val application: Application
) : NotificationDomainRepository {
    override suspend fun createChannel(): Boolean = withContext(dispatcherProvider.io) {
        val name = "Go 4 Lunch !"
        val descriptionText = ""
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("Go4Lunch", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        try {
            notificationManager.createNotificationChannel(channel)
            true
        } catch (e: Exception) {
            ensureActive()
            false
        }
        
    }
    
    override suspend fun notify(username: String, uid: String): Boolean =
        withContext(dispatcherProvider.io) {
            val builder = NotificationCompat.Builder(application.applicationContext, "Go4Lunch")
                .setSmallIcon(R.drawable.lunchbox)
                .setContentTitle("Bonjour $username")
                .setContentText("Voici ton UID : $uid")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            
            try {
                NotificationManagerCompat.from(application.applicationContext)
                    .notify(0, builder.build())
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
            
        }
    
}