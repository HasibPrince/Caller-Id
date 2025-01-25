package com.hasib.callerid.domian.usecases

import com.hasib.callerid.data.model.Contact
import com.hasib.callerid.data.model.Result
import com.hasib.callerid.data.model.doOnSuccess
import com.hasib.callerid.data.repositories.BlockedNumberRepository
import com.hasib.callerid.data.repositories.ContactRepository
import javax.inject.Inject

class FetchContactsUseCase @Inject constructor(
    private val contactsRepository: ContactRepository,
    private val blockedNumberRepository: BlockedNumberRepository
) {
    suspend operator fun invoke(): Result<List<Contact>> {
        val contactsResult = contactsRepository.fetchContacts()
        contactsResult.doOnSuccess {
            it.forEach { contact ->
                blockedNumberRepository.isBlocked(contact.phoneNumber).doOnSuccess { isBlocked ->
                    contact.isBlocked = isBlocked
                }
            }
        }

        return contactsResult
    }
}