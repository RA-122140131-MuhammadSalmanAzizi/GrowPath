package com.example.growpath.data.repository

import com.example.growpath.data.model.Milestone
import com.example.growpath.data.model.Note
import kotlinx.coroutines.flow.Flow

interface MilestoneRepository {
    fun getMilestonesByRoadmapId(roadmapId: String): Flow<List<Milestone>>
    fun getMilestoneById(milestoneId: String): Flow<Milestone?>
    suspend fun createMilestone(milestone: Milestone)
    suspend fun updateMilestone(milestone: Milestone)
    suspend fun deleteMilestone(milestoneId: String)
    suspend fun completeMilestone(milestoneId: String, isCompleted: Boolean)

    // Note operations
    fun getNotesByMilestoneId(milestoneId: String): Flow<List<Note>>
    suspend fun createNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(noteId: String)
}
