package com.hasib.callerid

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CallerIdApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}