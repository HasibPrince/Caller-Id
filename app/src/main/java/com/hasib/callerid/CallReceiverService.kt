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
//        createNotificationChannel()
    }

    override fun onCallRemoved(call: Call?) {
        super.onCallRemoved(call)
//        clearNotification()
        Log.d("CallReceiverService", "onCallRemoved: ${call?.details?.handle?.schemeSpecificPart}")
    }
}