package com.example.growpath.di

import android.content.Context
import androidx.room.Room
import com.example.growpath.data.local.AppDatabase
import com.example.growpath.data.remote.AuthService
import com.example.growpath.data.remote.FirebaseService
import com.example.growpath.data.remote.FirestoreService
import com.example.growpath.data.remote.StorageService
import com.example.growpath.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "growpath_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase) = database.userDao()

    @Provides
    @Singleton
    fun provideRoadmapDao(database: AppDatabase) = database.roadmapDao()

    @Provides
    @Singleton
    fun provideMilestoneDao(database: AppDatabase) = database.milestoneDao()

    @Provides
    @Singleton
    fun provideNoteDao(database: AppDatabase) = database.noteDao()

    @Provides
    @Singleton
    fun provideFirebaseService(): FirebaseService = FirebaseService()

    @Provides
    @Singleton
    fun provideAuthService(firebaseService: FirebaseService): AuthService = AuthService(firebaseService)

    @Provides
    @Singleton
    fun provideFirestoreService(firebaseService: FirebaseService): FirestoreService = FirestoreService(firebaseService)

    @Provides
    @Singleton
    fun provideStorageService(firebaseService: FirebaseService): StorageService = StorageService(firebaseService)

    @Provides
    @Singleton
    fun provideUserRepository(
        userDao: com.example.growpath.data.local.UserDao,
        authService: AuthService,
        firestoreService: FirestoreService
    ): UserRepository = UserRepositoryImpl(userDao, authService, firestoreService)

    @Provides
    @Singleton
    fun provideRoadmapRepository(
        roadmapDao: com.example.growpath.data.local.RoadmapDao,
        firestoreService: FirestoreService
    ): RoadmapRepository = RoadmapRepositoryImpl(roadmapDao, firestoreService)

    @Provides
    @Singleton
    fun provideMilestoneRepository(
        milestoneDao: com.example.growpath.data.local.MilestoneDao,
        noteDao: com.example.growpath.data.local.NoteDao,
        firestoreService: FirestoreService
    ): MilestoneRepository = MilestoneRepositoryImpl(milestoneDao, noteDao, firestoreService)
}
