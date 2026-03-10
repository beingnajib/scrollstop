package com.scrollstop.app.util

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.scrollstop.app.MainActivity
import com.scrollstop.app.R

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        if (!AccessibilityUtil.isServiceEnabled(context)) {
            showReEnableNotification(context)
        }
    }

    private fun showReEnableNotification(context: Context) {
        val openIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, openIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_SERVICE)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("ScrollStop Protection Inactive")
            .setContentText("Tap to re-enable doomscrolling protection")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context)
                .notify(Constants.NOTIFICATION_ID_SERVICE, notification)
        } catch (_: SecurityException) {
            // Missing POST_NOTIFICATIONS permission on Android 13+
        }
    }
}
