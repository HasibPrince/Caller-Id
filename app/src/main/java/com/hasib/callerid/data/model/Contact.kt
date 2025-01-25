package com.hasib.callerid.data.model

data class Contact(val name: String, val phoneNumber: String, var isBlocked: Boolean = false) {
    fun toggleBlocked() {
        isBlocked = !isBlocked
    }
}
