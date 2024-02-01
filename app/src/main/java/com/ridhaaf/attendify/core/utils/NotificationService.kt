package com.ridhaaf.attendify.core.utils

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import kotlin.random.Random

class NotificationService(
    private val context: Context,
) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun showBasicNotification(
        title: String,
        message: String,
    ) {
        val notification =
            NotificationCompat.Builder(context, "attendify_notification").setContentTitle(title)
                .setContentText(message).setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationManager.IMPORTANCE_HIGH).setAutoCancel(true).build()

        notificationManager.notify(
            Random.nextInt(), notification
        )
    }
}