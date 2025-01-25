package com.hasib.callerid.data.repositories

import com.hasib.callerid.data.model.Contact
import com.hasib.callerid.data.model.Result

interface ContactRepository {
    suspend fun fetchContacts(): Result<List<Contact>>
    fun toggleBlockedNumber(phoneNumber: String)
}