package com.example.growpath.repository

import com.example.growpath.model.Milestone
import com.example.growpath.model.Roadmap
import kotlinx.coroutines.flow.Flow

interface RoadmapRepository {
    fun getRoadmaps(): Flow<List<Roadmap>> // Changed to return Flow and non-suspend
    // Tambah fungsi lain sesuai kebutuhan

    // Added based on ViewModel assumptions
    suspend fun getRoadmapTitle(roadmapId: String): String
    suspend fun getMilestonesForRoadmap(roadmapId: String): List<Milestone>
    suspend fun getMilestoneById(milestoneId: String): Milestone?
    suspend fun updateMilestoneCompletion(milestoneId: String, isCompleted: Boolean)

    // Tambahkan metode baru untuk mengelola catatan milestone
    suspend fun updateMilestoneNote(milestoneId: String, noteContent: String)
}
