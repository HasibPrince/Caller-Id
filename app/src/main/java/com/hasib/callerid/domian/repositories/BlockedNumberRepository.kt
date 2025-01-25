package com.hasib.callerid.domian.repositories

import com.hasib.callerid.domian.model.Result

interface BlockedNumberRepository {
    suspend fun insertBlockedNumber(phoneNumber: String)
    suspend fun isBlocked(phoneNumber: String): Boolean
    suspend fun deleteBlockedNumber(phoneNumber: String)
    suspend fun fetchBlockedNumbers(): List<String>
}