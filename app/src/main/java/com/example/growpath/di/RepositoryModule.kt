package com.example.growpath.di

import com.example.growpath.data.NotificationRepository
import com.example.growpath.data.UserPreferencesManager
import com.example.growpath.repository.AchievementRepository
import com.example.growpath.repository.RoadmapRepository
import com.example.growpath.repository.UserRepository
import com.example.growpath.repository.impl.DummyAchievementRepositoryImpl
import com.example.growpath.repository.impl.DummyRoadmapRepositoryImpl
import com.example.growpath.repository.impl.DummyUserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.Lazy

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRoadmapRepository(
        notificationRepository: NotificationRepository,
        userRepository: UserRepository,
        userPreferencesManager: UserPreferencesManager
    ): RoadmapRepository {
        return DummyRoadmapRepositoryImpl(notificationRepository, userRepository, userPreferencesManager)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userPreferencesManager: UserPreferencesManager,
        achievementRepository: AchievementRepository
    ): UserRepository {
        return DummyUserRepositoryImpl(userPreferencesManager, achievementRepository)
    }

    @Provides
    @Singleton
    fun provideAchievementRepository(
        userPreferencesManager: UserPreferencesManager,
        userRepository: Lazy<UserRepository>
    ): AchievementRepository {
        return DummyAchievementRepositoryImpl(userPreferencesManager, userRepository)
    }
}
