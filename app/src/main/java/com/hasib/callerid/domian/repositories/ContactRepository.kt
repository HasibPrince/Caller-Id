package com.hasib.callerid.domian.repositories

import com.hasib.callerid.domian.model.Contact
import com.hasib.callerid.domian.model.Result

interface ContactRepository {
    suspend fun fetchContacts(): List<Contact>
    fun toggleBlockedNumber(phoneNumber: String)
}