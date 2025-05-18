package com.example.growpath.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconUrl: String,
    val experiencePoints: Int,
    val requirementType: RequirementType,
    val requirementValue: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

enum class RequirementType {
    COMPLETE_ROADMAPS,
    COMPLETE_MILESTONES,
    REACH_LEVEL,
    STREAK_DAYS,
    ADD_NOTES
}
