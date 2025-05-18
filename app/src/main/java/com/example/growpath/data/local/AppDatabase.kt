package com.example.growpath.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.growpath.data.model.*

@Database(
    entities = [
        User::class,
        Roadmap::class,
        Milestone::class,
        Note::class,
        Achievement::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun roadmapDao(): RoadmapDao
    abstract fun milestoneDao(): MilestoneDao
    abstract fun noteDao(): NoteDao
    abstract fun achievementDao(): AchievementDao
}
