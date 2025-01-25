package com.hasib.callerid.data.repositories

import android.content.Context
import android.telephony.TelephonyManager
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.hasib.callerid.data.database.BlockedNumberDao
import com.hasib.callerid.data.model.BlockedNumber
import com.hasib.callerid.domian.repositories.BlockedNumberRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockedNumberRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val blockedNumberDao: BlockedNumberDao,
) : BlockedNumberRepository {

    private var networkRegion = ""

    init {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        networkRegion = telephonyManager.networkCountryIso.uppercase(Locale.getDefault())
    }

    override suspend fun insertBlockedNumber(phoneNumber: String) {
        blockedNumberDao.insert(BlockedNumber(phoneNumber))
    }

    override suspend fun isBlocked(phoneNumber: String): Boolean {
        val blockedNumbers = blockedNumberDao.getAllBlockedNumbers()
        blockedNumbers.find { areNumbersEqual(it.phoneNumber, phoneNumber, networkRegion) }?.let {
            return true
        }
        return false
    }

    override suspend fun deleteBlockedNumber(phoneNumber: String) {
        blockedNumberDao.delete(BlockedNumber(phoneNumber))
    }

    override suspend fun fetchBlockedNumbers(): List<String> {
        return blockedNumberDao.getAllBlockedNumbers().map { it.phoneNumber}
    }

    private fun areNumbersEqual(firstNumber: String, secondNumber: String, defaultRegion: String): Boolean {
        Timber.d("Comparing $firstNumber and $secondNumber")
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        try {
            val firstParsedNumber = phoneNumberUtil.parse(firstNumber, defaultRegion)
            val secondParsedNumber = phoneNumberUtil.parse(secondNumber, defaultRegion)
            Timber.d("Parsed numbers: $firstParsedNumber, $secondParsedNumber")
            return phoneNumberUtil.isNumberMatch(firstParsedNumber, secondParsedNumber) == PhoneNumberUtil.MatchType.EXACT_MATCH
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}