package com.hasib.callerid.domian.usecases

import com.hasib.callerid.domian.handleDataFetch
import com.hasib.callerid.domian.model.Contact
import com.hasib.callerid.domian.model.Result
import com.hasib.callerid.domian.repositories.BlockedNumberRepository
import com.hasib.callerid.domian.repositories.ContactRepository
import javax.inject.Inject

class FetchContactsUseCase @Inject constructor(
    private val contactsRepository: ContactRepository,
    private val blockedNumberRepository: BlockedNumberRepository
) {
    suspend operator fun invoke(): Result<List<Contact>> {
        return handleDataFetch { fetchContactList() }
    }

    private suspend fun fetchContactList(): List<Contact> {
        val contactsResult = contactsRepository.fetchContacts()
        val blockedNumbers = blockedNumberRepository.fetchBlockedNumbers()
        contactsResult.forEach { contact ->
            contact.isBlocked = blockedNumbers.contains(contact.phoneNumber)
        }

        return contactsResult
    }
}