package com.hasib.callerid.data.repositories

import com.hasib.callerid.data.database.BlockedNumberDao
import com.hasib.callerid.data.model.BlockedNumber
import com.hasib.callerid.data.service.TelephonyManagerHelper
import com.hasib.callerid.domian.repositories.BlockedNumberRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockedNumberRepositoryImpl @Inject constructor(
    private val telephonyManagerHelper: TelephonyManagerHelper,
    private val blockedNumberDao: BlockedNumberDao,
) : BlockedNumberRepository {

    override suspend fun insertBlockedNumber(phoneNumber: String) {
        blockedNumberDao.insert(BlockedNumber(phoneNumber))
    }

    override suspend fun isBlocked(phoneNumber: String): Boolean {
        val blockedNumbers = blockedNumberDao.getAllBlockedNumbers()
        blockedNumbers.find { telephonyManagerHelper.areNumbersEqual(it.phoneNumber, phoneNumber) }?.let {
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
}