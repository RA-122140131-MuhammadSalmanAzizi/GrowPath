package com.example.growpath.repository.impl

import com.example.growpath.data.UserPreferencesManager
import com.example.growpath.model.Achievement
import com.example.growpath.repository.AchievementRepository
import com.example.growpath.repository.UserRepository
import dagger.Lazy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DummyAchievementRepositoryImpl @Inject constructor(
    private val userPreferencesManager: UserPreferencesManager,
    private val userRepository: Lazy<UserRepository> // Changed to Lazy<UserRepository>
) : AchievementRepository {

    // Constants for achievement XP rewards
    companion object {
        const val XP_ACHIEVEMENT_UNLOCK = 50 // XP reward for unlocking an achievement
    }

    // List of all available achievements with their criteria
    private val allAchievements = listOf(
        Achievement(
            id = "ach_1",
            title = "First Steps",
            description = "Complete your first milestone",
            iconUrl = "", // Add icon URL when available
            isUnlocked = false
        ),
        Achievement(
            id = "ach_2",
            title = "Quick Learner",
            description = "Complete 5 milestones in a single day",
            iconUrl = "",
            isUnlocked = false
        ),
        Achievement(
            id = "ach_3",
            title = "Mastery",
            description = "Complete a full roadmap",
            iconUrl = "",
            isUnlocked = false
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
        ),
        Achievement(
            id = "ach_6",
            title = "Persistent Learner",
            description = "Complete 10 milestones",
            iconUrl = "",
            isUnlocked = false
        ),
        Achievement(
            id = "ach_7",
            title = "Learning Journey",
            description = "Complete 2 roadmaps",
            iconUrl = "",
            isUnlocked = false
        )
    )

    // Keep track of unlocked achievements to emit through the flow
    private val _unlockedAchievementsFlow = MutableStateFlow<List<Achievement>>(emptyList())

    // Track completed milestones today for the "Quick Learner" achievement
    private var milestonesCompletedToday = mutableMapOf<String, Long>()
    private val todayTimestamp = System.currentTimeMillis() / 86400000 * 86400000 // Midnight today

    // Flag to prevent multiple simultaneous checks
    private val isCheckingAchievements = AtomicBoolean(false)

    // Initialize with saved preferences
    init {
        refreshUnlockedAchievements()
    }

    override suspend fun getAllAchievements(): List<Achievement> {
        // Get the current unlocked state for each achievement
        val unlockedAchievements = userPreferencesManager.getUserAchievements()

        return allAchievements.map { achievement ->
            val isUnlocked = unlockedAchievements.contains(achievement.id)
            achievement.copy(
                isUnlocked = isUnlocked,
                unlockedAt = if (isUnlocked) getUnlockTimestamp(achievement.id) else null
            )
        }
    }

    private fun getUnlockTimestamp(achievementId: String): Long {
        // Always return the current time to show achievements as unlocked now
        return System.currentTimeMillis()
    }

    override suspend fun checkAndUnlockAchievements(): List<Achievement> {
        // Use atomic boolean to prevent concurrent checks
        if (!isCheckingAchievements.compareAndSet(false, true)) {
            return emptyList()
        }

        try {
            val newlyUnlocked = mutableListOf<Achievement>()
            val unlockedAchievements = userPreferencesManager.getUserAchievements()

            // Check each achievement criteria
            // First Steps - Complete 1 milestone
            if (!unlockedAchievements.contains("ach_1") &&
                userPreferencesManager.getCompletedMilestoneCount() >= 1) {
                unlockAchievement("ach_1")
                findAchievement("ach_1")?.let {
                    newlyUnlocked.add(it.copy(isUnlocked = true, unlockedAt = System.currentTimeMillis()))
                }
            }

            // Persistent Learner - Complete 10 milestones
            if (!unlockedAchievements.contains("ach_6") &&
                userPreferencesManager.getCompletedMilestoneCount() >= 10) {
                unlockAchievement("ach_6")
                findAchievement("ach_6")?.let {
                    newlyUnlocked.add(it.copy(isUnlocked = true, unlockedAt = System.currentTimeMillis()))
                }
            }

            // Quick Learner - Complete 5 milestones in a single day
            val milestonesTodayCount = milestonesCompletedToday.count { it.value >= todayTimestamp }
            if (!unlockedAchievements.contains("ach_2") && milestonesTodayCount >= 5) {
                unlockAchievement("ach_2")
                findAchievement("ach_2")?.let {
                    newlyUnlocked.add(it.copy(isUnlocked = true, unlockedAt = System.currentTimeMillis()))
                }
            }

            // Mastery - Complete a full roadmap
            if (!unlockedAchievements.contains("ach_3") &&
                userPreferencesManager.getCompletedRoadmapCount() >= 1) {
                unlockAchievement("ach_3")
                findAchievement("ach_3")?.let {
                    newlyUnlocked.add(it.copy(isUnlocked = true, unlockedAt = System.currentTimeMillis()))
                }
            }

            // Learning Journey - Complete 2 roadmaps
            if (!unlockedAchievements.contains("ach_7") &&
                userPreferencesManager.getCompletedRoadmapCount() >= 2) {
                unlockAchievement("ach_7")
                findAchievement("ach_7")?.let {
                    newlyUnlocked.add(it.copy(isUnlocked = true, unlockedAt = System.currentTimeMillis()))
                }
            }

            // Knowledge Explorer - Start 3 different roadmaps
            if (!unlockedAchievements.contains("ach_5") &&
                userPreferencesManager.getStartedRoadmapCount() >= 3) {
                unlockAchievement("ach_5")
                findAchievement("ach_5")?.let {
                    newlyUnlocked.add(it.copy(isUnlocked = true, unlockedAt = System.currentTimeMillis()))
                }
            }

            // Dedicated Student - Access the app for 7 consecutive days
            if (!unlockedAchievements.contains("ach_4") &&
                userPreferencesManager.getLoginStreak() >= 7) {
                unlockAchievement("ach_4")
                findAchievement("ach_4")?.let {
                    newlyUnlocked.add(it.copy(isUnlocked = true, unlockedAt = System.currentTimeMillis()))
                }
            }

            // Refresh the flow with all currently unlocked achievements
            if (newlyUnlocked.isNotEmpty()) {
                refreshUnlockedAchievements()
            }

            return newlyUnlocked
        } finally {
            isCheckingAchievements.set(false)
        }
    }

    private suspend fun unlockAchievement(achievementId: String) {
        userPreferencesManager.saveAchievementUnlocked(achievementId)
        // Give XP reward for unlocking an achievement - use .get() to access the actual UserRepository
        userRepository.get().addExperiencePoints(XP_ACHIEVEMENT_UNLOCK)
    }

    private fun findAchievement(id: String): Achievement? {
        return allAchievements.find { it.id == id }
    }

    private fun refreshUnlockedAchievements() {
        val unlockedIds = userPreferencesManager.getUserAchievements()
        val unlockedAchievements = allAchievements.filter { unlockedIds.contains(it.id) }
            .map { it.copy(isUnlocked = true, unlockedAt = getUnlockTimestamp(it.id)) }
        _unlockedAchievementsFlow.value = unlockedAchievements
    }

    override fun resetAchievements() {
        userPreferencesManager.resetAchievementProgress()
        milestonesCompletedToday.clear()
        refreshUnlockedAchievements()
    }

    override suspend fun recordMilestoneCompletion(milestoneId: String): List<Achievement> {
        // Increment milestone counter
        userPreferencesManager.incrementCompletedMilestoneCount()

        // Track milestone completion for today (for "Quick Learner" achievement)
        milestonesCompletedToday[milestoneId] = System.currentTimeMillis()

        // Check if any achievements were unlocked
        return checkAndUnlockAchievements()
    }

    override suspend fun recordRoadmapStarted(roadmapId: String): List<Achievement> {
        // Increment started roadmaps counter
        userPreferencesManager.incrementStartedRoadmapCount()

        // Check if any achievements were unlocked
        return checkAndUnlockAchievements()
    }

    override suspend fun recordRoadmapCompletion(roadmapId: String): List<Achievement> {
        // Increment completed roadmaps counter
        userPreferencesManager.incrementCompletedRoadmapCount()

        // Check if any achievements were unlocked
        return checkAndUnlockAchievements()
    }

    override fun getUnlockedAchievementsFlow(): Flow<List<Achievement>> {
        return _unlockedAchievementsFlow.asStateFlow()
    }

    override suspend fun recordLogin(): List<Achievement> {
        // Update login streak
        userPreferencesManager.updateLoginStreak()

        // Check if any achievements were unlocked
        return checkAndUnlockAchievements()
    }
}
