package com.example.growpath.repository.impl

import com.example.growpath.data.UserPreferencesManager
import com.example.growpath.model.Achievement
import com.example.growpath.model.User
import com.example.growpath.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DummyUserRepositoryImpl @Inject constructor(
    private val userPreferencesManager: UserPreferencesManager
) : UserRepository {

    // Constants
    companion object {
        const val MAX_DISPLAY_NAME_LENGTH = 20
        const val XP_PER_LEVEL = 100 // Jumlah XP yang diperlukan untuk naik 1 level
        const val XP_MILESTONE_COMPLETION = 25 // XP yang didapat saat menyelesaikan milestone
    }

    // Create a repository scope for launching coroutines
    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Create a mutable state flow to emit user data changes
    private val _userFlow = MutableStateFlow(
        User(
            id = "user_123",
            displayName = "Salman Azizi",
            email = "salman@example.com",
            photoUrl = null,
            level = 7,
            experience = 683
        )
    )

    // Make user mutable to allow updates
    private var _user: User
        get() = _userFlow.value
        set(value) {
            _userFlow.value = value
        }

    // Initialize with saved preferences if available
    init {
        // Launch a coroutine to initialize from preferences
        repositoryScope.launch {
            try {
                val savedName = userPreferencesManager.userNameFlow.first()
                val savedEmail = userPreferencesManager.userEmailFlow.first()
                val savedPhotoUrl = userPreferencesManager.userPhotoUrlFlow.first()
                val savedLevel = userPreferencesManager.userLevelFlow.first()
                val savedXp = userPreferencesManager.userXpFlow.first()

                if (savedName != null || savedEmail != null || savedPhotoUrl != null || savedLevel > 1 || savedXp > 0) {
                    _user = _user.copy(
                        displayName = savedName ?: _user.displayName,
                        email = savedEmail ?: _user.email,
                        photoUrl = savedPhotoUrl ?: _user.photoUrl,
                        level = savedLevel,
                        experience = savedXp
                    )
                } else {
                    // Save initial values to preferences
                    userPreferencesManager.saveUserName(_user.displayName)
                    userPreferencesManager.saveUserEmail(_user.email)
                    _user.photoUrl?.let { userPreferencesManager.saveUserPhotoUrl(it) }
                    userPreferencesManager.saveUserLevel(_user.level)
                    userPreferencesManager.saveUserXP(_user.experience)
                }
            } catch (e: Exception) {
                // Handle initialization error
            }
        }
    }

    private val achievements = listOf(
        Achievement(
            id = "ach_1",
            title = "First Steps",
            description = "Complete your first milestone",
            iconUrl = "",
            isUnlocked = true,
            unlockedAt = System.currentTimeMillis() - 86400000 * 5
        ),
        Achievement(
            id = "ach_2",
            title = "Quick Learner",
            description = "Complete 5 milestones in a single day",
            iconUrl = "",
            isUnlocked = true,
            unlockedAt = System.currentTimeMillis() - 86400000 * 2
        ),
        Achievement(
            id = "ach_3",
            title = "Mastery",
            description = "Complete a full roadmap",
            iconUrl = "",
            isUnlocked = true,
            unlockedAt = System.currentTimeMillis() - 86400000
        ),
        Achievement(
            id = "ach_4",
            title = "Dedicated Student",
            description = "Access the app for 7 consecutive days",
            iconUrl = "",
            isUnlocked = false
        ),
        Achievement(
            id = "ach_5",
            title = "Knowledge Explorer",
            description = "Start 3 different roadmaps",
            iconUrl = "",
            isUnlocked = false
        )
    )

    override fun getUserFlow(): Flow<User?> {
        return _userFlow.asStateFlow()
    }

    override fun getUser(): User? {
        return _user
    }

    override suspend fun getUserAchievements(): List<Achievement> {
        delay(500) // Simulate network delay
        return achievements
    }

    override suspend fun updateUserProfile(displayName: String): User {
        delay(800) // Simulate network delay

        // Validate display name length
        if (displayName.length > MAX_DISPLAY_NAME_LENGTH) {
            throw IllegalArgumentException("Display name cannot exceed $MAX_DISPLAY_NAME_LENGTH characters")
        }

        // Save to persistent storage
        userPreferencesManager.saveUserName(displayName)

        // Update in-memory model
        _user = _user.copy(displayName = displayName)
        return _user
    }

    override suspend fun updateProfilePhoto(photoUrl: String?): User {
        delay(800) // Simulate network delay

        // Save to persistent storage
        userPreferencesManager.saveUserPhotoUrl(photoUrl)

        // Update in-memory model
        _user = _user.copy(photoUrl = photoUrl)
        return _user
    }

    override suspend fun addExperiencePoints(xp: Int): User {
        delay(300) // Simulate network delay

        if (xp <= 0) {
            return _user // Tidak ada perubahan jika XP <= 0
        }

        // Update experience user
        val newExperience = _user.experience + xp
        _user = _user.copy(experience = newExperience)

        // Simpan XP terbaru ke SharedPreferences
        userPreferencesManager.saveUserXP(newExperience)

        // Cek apakah perlu naik level
        return checkAndProcessLevelUp()
    }

    override suspend fun checkAndProcessLevelUp(): User {
        // Hitung berapa level yang seharusnya dimiliki pengguna berdasarkan XP
        val xpPerLevel = XP_PER_LEVEL
        val totalLevelsEarned = (_user.experience / xpPerLevel) + 1 // +1 karena level mulai dari 1

        // Jika level seharusnya lebih tinggi, maka naikkan level pengguna
        if (totalLevelsEarned > _user.level) {
            _user = _user.copy(level = totalLevelsEarned)
            // Simpan level terbaru ke SharedPreferences
            userPreferencesManager.saveUserLevel(totalLevelsEarned)
            // Di aplikasi nyata, kita mungkin ingin menampilkan animasi/notifikasi kenaikan level di sini
        }

        return _user
    }

    // Implementasi metode autentikasi
    override suspend fun login(username: String, password: String): Boolean {
        delay(1000) // Simulate network delay for authentication
        return userPreferencesManager.verifyCredentials(username, password)
    }

    override suspend fun changeUsername(oldUsername: String, password: String, newUsername: String): Boolean {
        delay(800) // Simulate network delay

        // Verify current credentials first
        if (!userPreferencesManager.verifyCredentials(oldUsername, password)) {
            return false // Authentication failed with old credentials
        }

        // Save the new username, keeping the same password
        userPreferencesManager.saveLoginCredentials(newUsername, password)
        return true
    }

    override suspend fun changePassword(username: String, oldPassword: String, newPassword: String): Boolean {
        delay(800) // Simulate network delay

        // Verify current credentials first
        if (!userPreferencesManager.verifyCredentials(username, oldPassword)) {
            return false // Authentication failed with old credentials
        }

        // Save the new password, keeping the same username
        userPreferencesManager.saveLoginCredentials(username, newPassword)
        return true
    }

    override fun getCurrentUsername(): String? {
        return userPreferencesManager.getLoginUsername()
    }

    override fun getCurrentUserId(): String? {
        // Untuk implementasi dummy, kita bisa mengembalikan ID dari user yang sedang aktif
        return _user.id
    }
}
