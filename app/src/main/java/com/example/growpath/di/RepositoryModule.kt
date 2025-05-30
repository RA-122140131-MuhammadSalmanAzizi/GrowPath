package com.example.growpath.di

import com.example.growpath.data.NotificationRepository
import com.example.growpath.data.UserPreferencesManager
import com.example.growpath.repository.RoadmapRepository
import com.example.growpath.repository.UserRepository
import com.example.growpath.repository.impl.DummyRoadmapRepositoryImpl
import com.example.growpath.repository.impl.DummyUserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRoadmapRepository(notificationRepository: NotificationRepository, userRepository: UserRepository): RoadmapRepository {
        return DummyRoadmapRepositoryImpl(notificationRepository, userRepository)
    }

    @Provides
    @Singleton
    fun provideUserRepository(userPreferencesManager: UserPreferencesManager): UserRepository {
        return DummyUserRepositoryImpl(userPreferencesManager)
    }
}
