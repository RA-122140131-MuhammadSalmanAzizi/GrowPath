package com.example.growpath.data.repository

import com.example.growpath.data.model.Roadmap
import kotlinx.coroutines.flow.Flow

interface RoadmapRepository {
    fun getRoadmapsByUserId(userId: String): Flow<List<Roadmap>>
    fun getRoadmapById(roadmapId: String): Flow<Roadmap?>
    suspend fun createRoadmap(roadmap: Roadmap)
    suspend fun updateRoadmap(roadmap: Roadmap)
    suspend fun deleteRoadmap(roadmapId: String)
    suspend fun updateProgress(roadmapId: String, progress: Float)
}
