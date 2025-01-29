package com.hasib.callerid.service

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.hasib.callerid.R
import com.hasib.callerid.domian.model.ValidationStatus
import com.hasib.callerid.domian.usecases.ValidateCallUseCase
import com.hasib.callerid.utils.NotificationViewer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CallerIdScreeningService : CallScreeningService() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    @Inject
    lateinit var telephonyManager: TelephonyManager

    @Inject
    lateinit var validationUseCase: ValidateCallUseCase

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle.schemeSpecificPart
        Timber.d("Incoming call from: $phoneNumber")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val isIncoming = callDetails.callDirection == Call.Details.DIRECTION_INCOMING
            if (isIncoming) {
                validateCall(callDetails)
            }
        } else {
            checkCallStatus(callDetails)
        }
    }

    private fun checkCallStatus(callDetails: Call.Details) {
        telephonyManager.listen(object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String) {
                super.onCallStateChanged(state, phoneNumber)
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    validateCall(callDetails)
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE)
    }

    private fun validateCall(callDetails: Call.Details) {
        coroutineScope.launch {
            val phoneNumber = callDetails.handle.schemeSpecificPart

            val (validationStatus, name) = validationUseCase.invoke(phoneNumber)
            when (validationStatus) {
                ValidationStatus.BLOCKED -> {
                    respondCall(callDetails, true)
                }

                ValidationStatus.SPECIAL -> {
                    handleSpecialCall(callDetails, name, phoneNumber)
                }

                ValidationStatus.ACCEPTED -> {
                    respondCall(callDetails, false)
                }
            }
        }

    }

    private fun handleSpecialCall(
        callDetails: Call.Details,
        name: String,
        phoneNumber: String?
    ) {
        respondCall(callDetails, false)
        val message =
            getString(R.string.message_incoming_call, name, phoneNumber)

        NotificationViewer.showNotification(
            this@CallerIdScreeningService,
            getString(R.string.title_incoming_call), message
        )
    }

    private fun respondCall(callDetails: Call.Details, isDisallowedCall: Boolean) {
        val response = CallResponse.Builder()
            .setDisallowCall(isDisallowedCall)
            .build()
        respondToCall(callDetails, response)
    }
}

