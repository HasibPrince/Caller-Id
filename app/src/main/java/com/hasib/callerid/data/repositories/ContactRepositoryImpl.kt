package com.hasib.callerid.data.repositories

import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import com.hasib.callerid.domian.handleDataFetch
import com.hasib.callerid.domian.model.Contact
import com.hasib.callerid.domian.model.Result
import com.hasib.callerid.domian.model.doOnSuccess
import com.hasib.callerid.domian.repositories.ContactRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepositoryImpl @Inject constructor(@ApplicationContext private val context: Context) :
    ContactRepository {

    private val contactList = mutableListOf<Contact>()

    override suspend fun fetchContacts(): List<Contact> {
        getContacts(context.contentResolver).doOnSuccess {
            contactList.clear()
            contactList.addAll(it)
        }
        return contactList
    }

    override fun toggleBlockedNumber(phoneNumber: String) {
        contactList.filter { it.phoneNumber == phoneNumber }.forEach {
            it.isBlocked = !it.isBlocked
        }
    }

    private suspend fun getContacts(contentResolver: ContentResolver): Result<List<Contact>> {
        return withContext(Dispatchers.IO) {
            handleDataFetch {
                performQuery(contentResolver)
            }
        }
    }

    private fun performQuery(contentResolver: ContentResolver): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val sortOrder = """
        CASE 
            WHEN ${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} GLOB '[A-Za-z]*' THEN 0
            ELSE 1
        END, ${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC
        """

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null, null, sortOrder
        )
        Timber.d("Cursor: ${cursor?.count}")
        cursor?.use {
            val nameIndex =
                it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex =
                it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val phoneNumber = it.getString(numberIndex)
                val contact = Contact(name, phoneNumber)
                contacts.add(contact)
            }
        }

        Timber.d("Contacts: ${contacts.size}")
        return contacts
    }
}