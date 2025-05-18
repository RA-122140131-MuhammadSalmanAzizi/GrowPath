package com.example.growpath.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "milestones",
    foreignKeys = [
        ForeignKey(
            entity = Roadmap::class,
            parentColumns = ["id"],
            childColumns = ["roadmapId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Milestone(
    @PrimaryKey val id: String,
    val roadmapId: String,
    val title: String,
    val description: String,
    val position: Int,
    val xPosition: Float,
    val yPosition: Float,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val experiencePoints: Int = 10,
    val dependencies: List<String> = emptyList(), // IDs of prerequisite milestones
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
