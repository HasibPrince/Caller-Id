package com.hasib.callerid.di

import android.content.Context
import com.hasib.callerid.data.database.BlockedNumberDao
import com.hasib.callerid.data.database.CallerIdDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun getDatabase(@ApplicationContext context: Context): BlockedNumberDao {
        return CallerIdDatabase.getDatabase(context).blockedNumberDao()
    }
}