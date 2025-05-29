package com.example.growpath.di

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
    fun provideRoadmapRepository(): RoadmapRepository {
        return DummyRoadmapRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return DummyUserRepositoryImpl()
    }
}
