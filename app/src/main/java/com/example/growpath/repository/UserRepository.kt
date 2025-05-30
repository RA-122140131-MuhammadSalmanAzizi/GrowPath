package com.example.growpath.repository

import com.example.growpath.model.Achievement
import com.example.growpath.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    // Get user as a Flow to receive live updates
    fun getUserFlow(): Flow<User?>

    // Get user as a one-time call (for backward compatibility)
    fun getUser(): User?

    suspend fun getUserAchievements(): List<Achievement>

    // Update user profile and emit the change to all collectors
    suspend fun updateUserProfile(displayName: String): User

    // Update user profile photo
    suspend fun updateUserPhoto(photoUrl: String?): User

    // Add experience points to user and return updated user
    suspend fun addExperiencePoints(xp: Int): User

    // Level up user if they have enough experience
    suspend fun checkAndProcessLevelUp(): User
}
