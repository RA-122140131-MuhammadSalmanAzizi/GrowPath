package com.example.growpath.data.local

import androidx.room.*
import com.example.growpath.data.model.Milestone
import kotlinx.coroutines.flow.Flow

@Dao
interface MilestoneDao {
    @Query("SELECT * FROM milestones WHERE roadmapId = :roadmapId ORDER BY position ASC")
    fun getMilestonesByRoadmapId(roadmapId: String): Flow<List<Milestone>>

    @Query("SELECT * FROM milestones WHERE id = :milestoneId")
    fun getMilestoneById(milestoneId: String): Flow<Milestone?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: Milestone)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<Milestone>)

    @Update
    suspend fun updateMilestone(milestone: Milestone)

    @Delete
    suspend fun deleteMilestone(milestone: Milestone)

    @Query("UPDATE milestones SET isCompleted = :isCompleted, completedAt = :completedAt, updatedAt = :timestamp WHERE id = :milestoneId")
    suspend fun updateMilestoneCompletion(milestoneId: String, isCompleted: Boolean, completedAt: Long?, timestamp: Long = System.currentTimeMillis())
}
