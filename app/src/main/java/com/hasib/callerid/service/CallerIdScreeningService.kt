package com.hasib.callerid.service

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import com.hasib.callerid.NotificationViewer
import com.hasib.callerid.data.repositories.SpecialContactsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CallerIdScreeningService : CallScreeningService() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
    private lateinit var telephonyManager: TelephonyManager

    @Inject
    lateinit var specialContactsRepository: SpecialContactsRepository

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
        coroutineScope.launch {
            val phoneNumber = callDetails.handle.schemeSpecificPart
            val specialContact = specialContactsRepository.fetchSpecialContactByPhoneNumber(phoneNumber)
            if (specialContact != null) {
                val response = CallResponse.Builder()
                    .setDisallowCall(true)
                    .build()
                respondToCall(callDetails, response)
                val message = "Name: ${specialContact.name} Phone Number: $phoneNumber"
                NotificationViewer.showNotification(this@CallerIdScreeningService, "Incoming Call", message)
            } else {
                val response = CallResponse.Builder()
                    .setDisallowCall(false)
                    .build()
                respondToCall(callDetails, response)
            }

        }

    }
}

