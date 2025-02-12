package com.hasib.callerid.di

import com.hasib.callerid.data.repositories.BlockedNumberRepositoryImpl
import com.hasib.callerid.data.repositories.ContactRepositoryImpl
import com.hasib.callerid.data.repositories.SpecialContactRepositoryImpl
import com.hasib.callerid.domian.repositories.BlockedNumberRepository
import com.hasib.callerid.domian.repositories.ContactRepository
import com.hasib.callerid.domian.repositories.SpecialContactsRepository
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

    @Singleton
    @Binds
    fun getSpecialContactRepository(specialContactsRepository: SpecialContactRepositoryImpl): SpecialContactsRepository

    @Singleton
    @Binds
    fun getBlockedNumberRepository(blockedNumberRepositoryImpl: BlockedNumberRepositoryImpl): BlockedNumberRepository
}