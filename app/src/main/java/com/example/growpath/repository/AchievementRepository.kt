package com.example.growpath.repository

import com.example.growpath.model.Achievement
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing achievements.
 */
interface AchievementRepository {
    /**
     * Get all available achievements with their unlock status.
     */
    suspend fun getAllAchievements(): List<Achievement>

    /**
     * Check if specific achievement requirements are met and unlock them if needed.
     * Returns list of newly unlocked achievements.
     */
    suspend fun checkAndUnlockAchievements(): List<Achievement>

    /**
     * Reset all achievement progress for a user.
     */
    fun resetAchievements()

    /**
     * Record that a milestone was completed (for tracking achievement progress).
     */
    suspend fun recordMilestoneCompletion(milestoneId: String): List<Achievement>

    /**
     * Record that a roadmap was started (for tracking achievement progress).
     */
    suspend fun recordRoadmapStarted(roadmapId: String): List<Achievement>

    /**
     * Record that a roadmap was completed (for tracking achievement progress).
     */
    suspend fun recordRoadmapCompletion(roadmapId: String): List<Achievement>

    /**
     * Get observable flow of unlocked achievements.
     */
    fun getUnlockedAchievementsFlow(): Flow<List<Achievement>>

    /**
     * Record a login (for tracking login streak achievements).
     */
    suspend fun recordLogin(): List<Achievement>
}
