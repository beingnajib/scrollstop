package com.scrollstop.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationHelper {

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java)

        val serviceChannel = NotificationChannel(
            Constants.CHANNEL_SERVICE,
            "Protection Status",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows when ScrollStop protection is active"
            setShowBadge(false)
        }

        val blocksChannel = NotificationChannel(
            Constants.CHANNEL_BLOCKS,
            "Block Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifies when content is blocked"
        }

        val reportsChannel = NotificationChannel(
            Constants.CHANNEL_REPORTS,
            "Daily Reports",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Daily and weekly usage summaries"
        }

        manager.createNotificationChannels(
            listOf(serviceChannel, blocksChannel, reportsChannel)
        )
    }
}
