package com.hasib.callerid.data.service

import android.telephony.TelephonyManager
import com.google.i18n.phonenumbers.PhoneNumberUtil
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TelephonyManagerHelper @Inject constructor(telephonyManager: TelephonyManager) {

    private var networkRegion = ""

    init {
        networkRegion = telephonyManager.networkCountryIso.uppercase(Locale.getDefault())
    }

    fun areNumbersEqual(firstNumber: String, secondNumber: String): Boolean {
        Timber.d("Comparing for $networkRegion:  $firstNumber and $secondNumber")
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        try {
            val firstParsedNumber = phoneNumberUtil.parse(firstNumber, networkRegion)
            val secondParsedNumber = phoneNumberUtil.parse(secondNumber, networkRegion)
            Timber.d("Parsed numbers: $firstParsedNumber, $secondParsedNumber")
            return phoneNumberUtil.isNumberMatch(firstParsedNumber, secondParsedNumber) == PhoneNumberUtil.MatchType.EXACT_MATCH
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

}