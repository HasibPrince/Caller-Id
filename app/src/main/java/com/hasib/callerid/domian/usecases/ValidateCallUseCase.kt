package com.hasib.callerid.domian.usecases

import com.hasib.callerid.domian.model.ValidationStatus
import com.hasib.callerid.domian.repositories.BlockedNumberRepository
import com.hasib.callerid.domian.repositories.SpecialContactsRepository
import timber.log.Timber
import javax.inject.Inject

class ValidateCallUseCase @Inject constructor(
    private val specialContactsRepository: SpecialContactsRepository,
    private val blockedNumberRepository: BlockedNumberRepository
) {
    suspend operator fun invoke(phoneNumber: String): Pair<ValidationStatus, String> {
        val isBlocked = blockedNumberRepository.isBlocked(phoneNumber)
        if (isBlocked) {
            Timber.d("Blocked call from: $phoneNumber")
            return Pair(ValidationStatus.BLOCKED, "")
        }

        val specialContact = specialContactsRepository.fetchSpecialContactByPhoneNumber(phoneNumber)
        if (specialContact != null) {
            Timber.d("Special call from: $phoneNumber")
            return Pair(ValidationStatus.SPECIAL, specialContact.name)
        }

        return Pair(ValidationStatus.ACCEPTED, "")
    }

}