package com.despaircorp.data.notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.despaircorp.data.R
import com.despaircorp.domain.notifications.NotificationDomainRepository
import javax.inject.Inject

class NotificationDataRepository @Inject constructor(
    private val application: Application,
    private val notificationManagerCompat: NotificationManagerCompat,
) : NotificationDomainRepository {
    override fun createChannel() {
        val name = "Go 4 Lunch !"
        val descriptionText = ""
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("Go4Lunch", name, importance).apply {
            description = descriptionText
        }
        notificationManagerCompat.createNotificationChannel(channel)
    }
    
    override fun notify(username: String, sentence: String) {
        val builder = NotificationCompat.Builder(application, "Go4Lunch")
            .setSmallIcon(R.drawable.lunchbox)
            .setContentTitle("Bonjour $username")
            .setContentText(sentence)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        
        notificationManagerCompat.notify(0, builder.build())
    }
    
}