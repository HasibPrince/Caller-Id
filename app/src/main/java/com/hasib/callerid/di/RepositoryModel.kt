package com.hasib.callerid.di

import com.hasib.callerid.data.repositories.ContactRepository
import com.hasib.callerid.data.repositories.ContactRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    fun getContactRepository(contactRepositoryImpl: ContactRepositoryImpl): ContactRepository
}