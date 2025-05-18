package com.example.growpath.data.local

import androidx.room.*
import com.example.growpath.data.model.Roadmap
import kotlinx.coroutines.flow.Flow

@Dao
interface RoadmapDao {
    @Query("SELECT * FROM roadmaps WHERE userId = :userId")
    fun getRoadmapsByUserId(userId: String): Flow<List<Roadmap>>

    @Query("SELECT * FROM roadmaps WHERE id = :roadmapId")
    fun getRoadmapById(roadmapId: String): Flow<Roadmap?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoadmap(roadmap: Roadmap)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoadmaps(roadmaps: List<Roadmap>)

    @Update
    suspend fun updateRoadmap(roadmap: Roadmap)

    @Delete
    suspend fun deleteRoadmap(roadmap: Roadmap)

    @Query("UPDATE roadmaps SET progress = :progress, updatedAt = :timestamp WHERE id = :roadmapId")
    suspend fun updateProgress(roadmapId: String, progress: Float, timestamp: Long = System.currentTimeMillis())
}
