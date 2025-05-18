package com.example.growpath.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roadmaps")
data class Roadmap(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val category: String,
    val difficulty: Int,
    val estimatedDuration: Int, // in days
    val progress: Float = 0f,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPublic: Boolean = false
)
