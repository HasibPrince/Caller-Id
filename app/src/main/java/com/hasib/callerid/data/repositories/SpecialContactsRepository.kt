package com.hasib.callerid.data.repositories

import com.hasib.callerid.data.model.Contact

interface SpecialContactsRepository {
    suspend fun fetchSpecialContactByPhoneNumber(phoneNumber: String): Contact?
}