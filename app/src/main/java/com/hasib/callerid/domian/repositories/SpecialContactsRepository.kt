package com.hasib.callerid.domian.repositories

import com.hasib.callerid.domian.model.Contact

interface SpecialContactsRepository {
    suspend fun fetchSpecialContactByPhoneNumber(phoneNumber: String): Contact?
}