package com.hasib.callerid.service

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import com.hasib.callerid.NotificationViewer
import com.hasib.callerid.R
import com.hasib.callerid.data.model.Result
import com.hasib.callerid.data.repositories.BlockedNumberRepository
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

    @Inject
    lateinit var blockedNumberRepository: BlockedNumberRepository

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
                validateCall(callDetails)
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
                    validateCall(callDetails)
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE)
    }

    private fun validateCall(callDetails: Call.Details) {
        coroutineScope.launch {
            val phoneNumber = callDetails.handle.schemeSpecificPart

            val result = blockedNumberRepository.isBlocked(phoneNumber)
            if (result is Result.Success) {
                if (result.data) {
                    Log.d("CallerIdScreeningService", "Blocked call from: $phoneNumber")
                    respondCall(callDetails, true)
                    return@launch
                }
            }

            val specialContact = specialContactsRepository.fetchSpecialContactByPhoneNumber(phoneNumber)
            if (specialContact != null) {
                respondCall(callDetails, true)
                val message =
                    getString(R.string.message_incoming_call, specialContact.name, phoneNumber)
                NotificationViewer.showNotification(this@CallerIdScreeningService, "Incoming Call", message)
            } else {
                respondCall(callDetails, false)
            }

        }

    }

    private fun respondCall(callDetails: Call.Details, isDisallowCall: Boolean) {
        val response = CallResponse.Builder()
            .setDisallowCall(isDisallowCall)
            .build()
        respondToCall(callDetails, response)
    }
}

