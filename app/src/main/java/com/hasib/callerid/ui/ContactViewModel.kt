package com.hasib.callerid.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasib.callerid.domian.model.Contact
import com.hasib.callerid.domian.model.Result
import com.hasib.callerid.domian.usecases.FetchContactsUseCase
import com.hasib.callerid.domian.usecases.ToggleBlockNumberUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val fetchContactsUseCase: FetchContactsUseCase,
    private val toggleBlockNumberUseCase: ToggleBlockNumberUseCase
) : ViewModel() {
    private val _contactsLiveData = MutableLiveData<Result<List<Contact>>>()
    val contactsLiveData: LiveData<Result<List<Contact>>> = _contactsLiveData

    private val _blockContactsLiveData = MutableLiveData<Contact>()
    val blockContactsLiveData: LiveData<Contact> = _blockContactsLiveData

    fun fetchContacts() {
        _contactsLiveData.value = Result.Loading
        viewModelScope.launch {
            val contacts = fetchContactsUseCase.invoke()
            _contactsLiveData.value = contacts
        }
    }

    fun blockNumber(contact: Contact) {
        viewModelScope.launch {
            toggleBlockNumberUseCase.invoke(contact.phoneNumber)
            _blockContactsLiveData.value = contact
        }
    }
}