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
        val name = application.getString(R.string.app_name)
        val descriptionText = ""
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel =
            NotificationChannel(application.getString(R.string.app_name), name, importance).apply {
                description = descriptionText
            }
        notificationManagerCompat.createNotificationChannel(channel)
    }
    
    override fun notify(username: String, sentence: String) {
        val builder =
            NotificationCompat.Builder(application, application.getString(R.string.app_name))
                .setSmallIcon(R.drawable.lunchbox)
                .setContentTitle(
                    StringBuilder()
                        .append(R.string.hello)
                        .append(" ")
                        .append(username)
                )
                .setContentText(sentence)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
        
        notificationManagerCompat.notify(0, builder.build())
    }
    
}