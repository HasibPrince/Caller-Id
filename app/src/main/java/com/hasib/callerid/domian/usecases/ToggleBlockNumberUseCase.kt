package com.hasib.callerid.domian.usecases

import com.hasib.callerid.domian.repositories.BlockedNumberRepository
import com.hasib.callerid.domian.repositories.ContactRepository
import javax.inject.Inject

class ToggleBlockNumberUseCase @Inject constructor(
    private val blockedNumberRepository: BlockedNumberRepository,
    private val contactRepository: ContactRepository
) {
    suspend operator fun invoke(phoneNumber: String) {
        val isBlocked = blockedNumberRepository.isBlocked(phoneNumber)
        if (isBlocked) {
            blockedNumberRepository.deleteBlockedNumber(phoneNumber)
        } else {
            blockedNumberRepository.insertBlockedNumber(phoneNumber)
        }
        contactRepository.toggleBlockedNumber(phoneNumber)
    }
}

