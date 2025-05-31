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
    suspend fun updateProfilePhoto(photoUrl: String?): User

    // Add experience points to user and return updated user
    suspend fun addExperiencePoints(xp: Int): User

    // Level up user if they have enough experience
    suspend fun checkAndProcessLevelUp(): User

    // Authentication methods
    suspend fun login(username: String, password: String): Boolean

    suspend fun changeUsername(oldUsername: String, password: String, newUsername: String): Boolean

    suspend fun changePassword(username: String, oldPassword: String, newPassword: String): Boolean

    fun getCurrentUsername(): String?

    // Get current user ID for storage operations
    fun getCurrentUserId(): String?
}
