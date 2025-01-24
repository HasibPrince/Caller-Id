package com.hasib.callerid.service

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.hasib.callerid.NotificationViewer

class CallerIdScreeningService : CallScreeningService() {

    private lateinit var telephonyManager: TelephonyManager

    override fun onCreate() {
        super.onCreate()
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
    }

    override fun onScreenCall(callDetails: Call.Details) {

        Log.d("CallerIdScreeningService", "onScreenCall")
        // Can check the direction of the call
        val phoneNumber = callDetails.handle.schemeSpecificPart
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d("CallerIdScreeningService", "Incoming call from: $phoneNumber")
            val isIncoming = callDetails.callDirection == Call.Details.DIRECTION_INCOMING
            if (isIncoming) {
                responseCall(callDetails)
            }
        }else {
            checkCallStatus(callDetails)
        }

        NotificationViewer.createNotificationChannel(this, "Incoming Call", "Incoming call from: $phoneNumber")
    }

    private fun checkCallStatus(callDetails: Call.Details) {
        telephonyManager.listen(object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String) {
                super.onCallStateChanged(state, phoneNumber)
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    Log.d("CallerIdScreeningService", "Incoming call from: $phoneNumber")
                    responseCall(callDetails)
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE)
    }

    private fun responseCall(callDetails: Call.Details) {
        val response = CallResponse.Builder()
            .setDisallowCall(true)
            .build()
        respondToCall(callDetails, response)
    }
}

