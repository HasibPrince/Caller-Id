package com.hasib.callerid.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BlockedNumber(@PrimaryKey val phoneNumber: String = "")
