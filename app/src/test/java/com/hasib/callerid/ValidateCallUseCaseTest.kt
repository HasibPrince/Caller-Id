package com.hasib.callerid
import com.hasib.callerid.domian.model.Contact
import com.hasib.callerid.domian.model.ValidationStatus
import com.hasib.callerid.domian.repositories.BlockedNumberRepository
import com.hasib.callerid.domian.repositories.SpecialContactsRepository
import com.hasib.callerid.domian.usecases.ValidateCallUseCase
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import kotlin.test.assertEquals

class ValidateCallUseCaseTest {

    private lateinit var validateCallUseCase: ValidateCallUseCase
    private val blockedNumberRepository: BlockedNumberRepository = mock()
    private val specialContactsRepository: SpecialContactsRepository = mock()

    @BeforeEach
    fun setUp() {
        validateCallUseCase = ValidateCallUseCase(specialContactsRepository, blockedNumberRepository)
    }

    @Test
    fun `should return BLOCKED when the phone number is blocked`() = runTest {
        val phoneNumber = "1234567890"

        `when`(blockedNumberRepository.isBlocked(phoneNumber)).thenReturn(true)

        val result = validateCallUseCase(phoneNumber)

        assertEquals(ValidationStatus.BLOCKED, result.first)
        verify(blockedNumberRepository).isBlocked(phoneNumber)
    }

    @Test
    fun `should return SPECIAL when the phone number is a special contact`() = runTest() {
        val phoneNumber = "9876543210"
        val specialContact = Contact("John Doe", phoneNumber)

        `when`(blockedNumberRepository.isBlocked(phoneNumber)).thenReturn(false)
        `when`(specialContactsRepository.fetchSpecialContactByPhoneNumber(phoneNumber)).thenReturn(specialContact)

        val result = validateCallUseCase(phoneNumber)

        assertEquals(ValidationStatus.SPECIAL, result.first)
        assertEquals("John Doe", result.second)
        verify(specialContactsRepository).fetchSpecialContactByPhoneNumber(phoneNumber)
    }

    @Test
    fun `should return ACCEPTED when the phone number is neither blocked nor special`() = runTest() {
        val phoneNumber = "5555555555"

        `when`(blockedNumberRepository.isBlocked(phoneNumber)).thenReturn(false)
        `when`(specialContactsRepository.fetchSpecialContactByPhoneNumber(phoneNumber)).thenReturn(null)

        val result = validateCallUseCase(phoneNumber)

        assertEquals(ValidationStatus.ACCEPTED, result.first)
        assertEquals("", result.second)
    }
}
