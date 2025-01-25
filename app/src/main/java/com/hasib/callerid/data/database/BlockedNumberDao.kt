package com.hasib.callerid.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BlockedNumberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(blockedNumber: String): Int

    @Query("SELECT EXISTS (SELECT 1 FROM BlockedNumber WHERE phoneNumber = :phoneNumber)")
    suspend fun exists(phoneNumber: String): Boolean

    @Delete
    suspend fun delete(blockedNumber: String): Int
}