package com.hasib.callerid.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasib.callerid.data.repositories.ContactRepository
import com.hasib.callerid.data.model.Contact
import com.hasib.callerid.data.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(private val contactRepository: ContactRepository) : ViewModel() {
    private val _contactsLiveData = MutableLiveData<Result<List<Contact>>>()
    val contactsLiveData: MutableLiveData<Result<List<Contact>>> = _contactsLiveData

    fun fetchContacts() {
        _contactsLiveData.value = Result.Loading
        viewModelScope.launch {
            val contacts = contactRepository.fetchContacts()
            _contactsLiveData.value = contacts
        }
    }
}