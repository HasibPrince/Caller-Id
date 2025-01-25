package com.hasib.callerid.data.model

import java.util.UUID

data class Contact(
    val name: String,
    val phoneNumber: String,
    var isBlocked: Boolean = false,
    val uId: String = UUID.randomUUID().toString()
)
