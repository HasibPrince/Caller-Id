package com.hasib.callerid

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.hasib.callerid.ui.MainActivity

object NotificationViewer {
    fun createNotificationChannel(context: Context, title: String, message: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            showNotification(context, title, message)
            return
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java);
        var notificationChannel = NotificationChannel(
            "YOUR_CHANNEL_ID", "Incoming Calls",
            NotificationManager.IMPORTANCE_MAX
        )

        notificationManager.createNotificationChannel(notificationChannel)

        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.flags = Intent.FLAG_ACTIVITY_NO_USER_ACTION or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.setClass(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = Notification.Builder(context, "YOUR_CHANNEL_ID");
        builder.setOngoing(true);
        builder.setContentIntent(pendingIntent);
        builder.setFullScreenIntent(pendingIntent, true)
        builder.setPriority(Notification.PRIORITY_HIGH)
        builder.setCategory(NotificationCompat.CATEGORY_CALL)
        builder.setAutoCancel(false)

        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setContentTitle(title);
        builder.setContentText(message)

        notificationManager.notify(1, builder.build());
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Create the notification using NotificationCompat
        val notification = NotificationCompat.Builder(context)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app's icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // For high importance
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Vibrate and play sound
            .setAutoCancel(false)
            .build()

        // Show the notification
        val notificationId = 1
        notificationManager.notify(notificationId, notification)
    }

    fun clearNotification(context: Context) {
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1);
    }
}