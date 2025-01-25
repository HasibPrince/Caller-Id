package com.hasib.callerid.data.repositories

import com.hasib.callerid.data.database.BlockedNumberDao
import com.hasib.callerid.data.handleDataFetch
import com.hasib.callerid.data.model.BlockedNumber
import com.hasib.callerid.data.model.Result
import com.hasib.callerid.data.model.doOnSuccess
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockedNumberRepositoryImpl @Inject constructor(private val blockedNumberDao: BlockedNumberDao) :
    BlockedNumberRepository {
    override suspend fun insertBlockedNumber(phoneNumber: String) {
        blockedNumberDao.insert(BlockedNumber(phoneNumber))
    }

    override suspend fun isBlocked(phoneNumber: String): Result<Boolean> {
       return handleDataFetch { blockedNumberDao.exists(phoneNumber) }
    }

    override suspend fun deleteBlockedNumber(phoneNumber: String) {
        blockedNumberDao.delete(BlockedNumber(phoneNumber))
    }

    override suspend fun toggleBlockedNumber(phoneNumber: String) {
        isBlocked(phoneNumber).doOnSuccess { isBlocked ->
            if (isBlocked) {
                deleteBlockedNumber(phoneNumber)
            } else {
                insertBlockedNumber(phoneNumber)
            }
        }
    }
}