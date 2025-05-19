package com.example.growpath

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GrowPathApp : Application() {
    companion object {
        lateinit var instance: GrowPathApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
