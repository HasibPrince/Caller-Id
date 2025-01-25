package com.hasib.callerid.data.repositories

import com.hasib.callerid.data.model.Result

interface BlockedNumberRepository {
    suspend fun insertBlockedNumber(phoneNumber: String)
    suspend fun isBlocked(phoneNumber: String): Result<Boolean>
    suspend fun deleteBlockedNumber(phoneNumber: String)
    suspend fun toggleBlockedNumber(phoneNumber: String)
}