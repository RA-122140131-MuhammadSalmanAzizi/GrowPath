package com.example.growpath.repository

import com.example.growpath.model.Milestone
import com.example.growpath.model.Roadmap

interface RoadmapRepository {
    fun getRoadmaps(): List<Roadmap>
    // Tambah fungsi lain sesuai kebutuhan

    // Added based on ViewModel assumptions
    suspend fun getRoadmapTitle(roadmapId: String): String // Added suspend
    suspend fun getMilestonesForRoadmap(roadmapId: String): List<Milestone> // Added suspend
    suspend fun getMilestoneById(milestoneId: String): Milestone? // Added suspend, made nullable
    suspend fun updateMilestoneCompletion(milestoneId: String, isCompleted: Boolean) // Added suspend

    // Tambahkan metode baru untuk mengelola catatan milestone
    suspend fun updateMilestoneNote(milestoneId: String, noteContent: String)
}
