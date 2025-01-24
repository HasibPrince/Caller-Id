package com.hasib.callerid.data.repositories

import com.hasib.callerid.data.model.Contact
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpecialContactRepositoryImpl @Inject constructor() : SpecialContactsRepository {
    private val specialContacts = listOf(
        Contact("Hasib", "12345"),
        Contact("Afsar", "123456"),
        Contact("Sagar", "1234567"))

    override suspend fun fetchSpecialContactByPhoneNumber(phoneNumber: String): Contact? {
        return specialContacts.find { it.phoneNumber == phoneNumber }
    }
}