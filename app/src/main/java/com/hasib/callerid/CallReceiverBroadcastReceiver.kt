package com.hasib.callerid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log

class CallReceiverBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Log.d("TAG", "onReceive: ${intent.action}")
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                Log.d("CallReceiver", "Incoming call from: $incomingNumber")
            } else if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                Log.d("CallReceiver", "Call received")
            } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                Log.d("CallReceiver", "Call ended")
                val telecomManager = context.getSystemService(TelecomManager::class.java)
            }
        }
    }
}