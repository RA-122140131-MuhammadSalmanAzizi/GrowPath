package com.example.growpath

import android.app.Application
import com.example.growpath.repository.RoadmapRepository
import com.example.growpath.repository.UserRepository
import com.example.growpath.repository.impl.DummyRoadmapRepositoryImpl
import com.example.growpath.repository.impl.DummyUserRepositoryImpl

class GrowPathApp : Application() {
    // Simple service locator pattern
    val userRepository: UserRepository by lazy { DummyUserRepositoryImpl() }
    val roadmapRepository: RoadmapRepository by lazy { DummyRoadmapRepositoryImpl() }

    companion object {
        lateinit var instance: GrowPathApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
