package com.hasib.callerid.data.repositories

import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import com.hasib.callerid.data.model.Contact
import com.hasib.callerid.data.model.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepositoryImpl @Inject constructor(@ApplicationContext private val context: Context) :
    ContactRepository {
    override suspend fun fetchContacts(): Result<List<Contact>> {
        return getContacts(context.contentResolver)
    }

    private suspend fun getContacts(contentResolver: ContentResolver): Result<List<Contact>> {
        return withContext(Dispatchers.IO) {
            try {
                performQuery(contentResolver)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    private fun performQuery(contentResolver: ContentResolver): Result.Success<MutableList<Contact>> {
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
        Log.d("MainActivity", "Cursor: ${cursor?.count}")
        cursor?.use {
            val nameIndex =
                it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex =
                it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val phoneNumber = it.getString(numberIndex)
                contacts.add(Contact(name, phoneNumber))
            }
        }

        Log.d("MainActivity", "Contacts: ${contacts.size}")
        return Result.Success(contacts)
    }
}