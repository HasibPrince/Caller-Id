package com.hasib.callerid.domian.usecases

import com.hasib.callerid.data.model.doOnSuccess
import com.hasib.callerid.data.repositories.BlockedNumberRepository
import com.hasib.callerid.data.repositories.ContactRepository
import javax.inject.Inject

class ToggleBlockNumberUser @Inject constructor(
    private val blockedNumberRepository: BlockedNumberRepository,
    private val contactRepository: ContactRepository
) {
    suspend operator fun invoke(phoneNumber: String) {
        blockedNumberRepository.isBlocked(phoneNumber).doOnSuccess { isBlocked ->
            if (isBlocked) {
                blockedNumberRepository.deleteBlockedNumber(phoneNumber)
            } else {
                blockedNumberRepository.insertBlockedNumber(phoneNumber)
            }
            contactRepository.toggleBlockedNumber(phoneNumber)
        }
    }
}

