package com.example.growpath.data.repository

import com.example.growpath.data.local.RoadmapDao
import com.example.growpath.data.model.Roadmap
import com.example.growpath.data.remote.FirestoreService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoadmapRepositoryImpl @Inject constructor(
    private val roadmapDao: RoadmapDao,
    private val firestoreService: FirestoreService
) : RoadmapRepository {

    override fun getRoadmapsByUserId(userId: String): Flow<List<Roadmap>> {
        return roadmapDao.getRoadmapsByUserId(userId)
    }

    override fun getRoadmapById(roadmapId: String): Flow<Roadmap?> {
        return roadmapDao.getRoadmapById(roadmapId)
    }

    override suspend fun createRoadmap(roadmap: Roadmap) {
        roadmapDao.insertRoadmap(roadmap)
        firestoreService.createRoadmap(roadmap)
    }

    override suspend fun updateRoadmap(roadmap: Roadmap) {
        roadmapDao.updateRoadmap(roadmap)
        firestoreService.updateRoadmap(roadmap)
    }

    override suspend fun deleteRoadmap(roadmapId: String) {
        val roadmap = roadmapDao.getRoadmapById(roadmapId).first()
        roadmap?.let {
            roadmapDao.deleteRoadmap(it)
            firestoreService.deleteRoadmap(roadmapId)
        }
    }

    override suspend fun updateProgress(roadmapId: String, progress: Float) {
        roadmapDao.updateProgress(roadmapId, progress)
        val roadmap = roadmapDao.getRoadmapById(roadmapId).first()
        roadmap?.let { firestoreService.updateRoadmap(it) }
    }
}
