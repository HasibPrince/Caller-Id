package com.hasib.callerid.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hasib.callerid.data.model.BlockedNumber

@Database(entities = [BlockedNumber::class], version = 1)
abstract class CallerIdDatabase : RoomDatabase() {
    abstract fun blockedNumberDao(): BlockedNumberDao

    companion object {
        const val DATABASE_NAME = "callerId_database"

        @Volatile
        private var INSTANCE: CallerIdDatabase? = null

        fun getDatabase(context: Context): CallerIdDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CallerIdDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}