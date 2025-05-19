package com.example.growpath.repository.impl

import com.example.growpath.model.Achievement
import com.example.growpath.model.User
import com.example.growpath.repository.UserRepository
import kotlinx.coroutines.delay

class DummyUserRepositoryImpl : UserRepository {

    private val user = User(
        id = "user_123",
        displayName = "Salman Azizi",
        email = "salman@example.com",
        photoUrl = null,
        level = 7,
        experience = 683
    )

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

    override fun getUser(): User? {
        return user
    }

    override suspend fun getUserAchievements(): List<Achievement> {
        delay(500) // Simulate network delay
        return achievements
    }

    override suspend fun updateUserProfile(displayName: String): User {
        delay(800) // Simulate network delay
        return user.copy(displayName = displayName)
    }
}
