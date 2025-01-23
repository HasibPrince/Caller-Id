package com.hasib.callerid

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.telecom.Call
import android.telecom.InCallService
import android.telecom.TelecomManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.hasib.callerid.ui.MainActivity

class CallReceiverService : InCallService() {

    override fun onCallAdded(call: Call?) {
        super.onCallAdded(call)
        Log.d("CallReceiverService", "onCallAdded: ${call?.details?.handle?.schemeSpecificPart}")
        createNotificationChannel()
    }

    override fun onCallRemoved(call: Call?) {
        super.onCallRemoved(call)

        clearNotification()
        Log.d("CallReceiverService", "onCallRemoved: ${call?.details?.handle?.schemeSpecificPart}")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "YOUR_CHANNEL_ID", "Incoming Calls",
            NotificationManager.IMPORTANCE_MAX
        )

        // other channel setup stuff goes here.

        // We'll use the default system ringtone for our incoming call notification channel.  You can
        // use your own audio resource here.
        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        channel.setSound(
            ringtoneUri, AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )

        val mgr = getSystemService(NotificationManager::class.java)
        mgr.createNotificationChannel(channel)

        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.flags = Intent.FLAG_ACTIVITY_NO_USER_ACTION or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.setClass(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = Notification.Builder(this, "YOUR_CHANNEL_ID");
        builder.setOngoing(true);
        builder.setContentIntent(pendingIntent);
        builder.setFullScreenIntent(pendingIntent, true)
        builder.setPriority(Notification.PRIORITY_HIGH)
        builder.setCategory(NotificationCompat.CATEGORY_CALL)
        builder.setAutoCancel(false)

        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setContentTitle("Your notification title");
        builder.setContentText("Your notification content.")

        val notificationManager = this.getSystemService(NotificationManager::class.java);
        notificationManager.notify(1, builder.build());
    }

    private fun clearNotification() {
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1);
    }
}