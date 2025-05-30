package com.example.growpath.repository

import com.example.growpath.model.Milestone
import com.example.growpath.model.Note
import com.example.growpath.model.Roadmap
import kotlinx.coroutines.flow.Flow

interface RoadmapRepository {
    // Returns a continuous Flow of roadmaps that will emit updates whenever data changes
    fun getRoadmaps(): Flow<List<Roadmap>>

    // Returns a Flow of roadmap details that will emit updates when the roadmap changes
    fun getRoadmapById(roadmapId: String): Flow<Roadmap?>

    // Returns a Flow of milestones for a specific roadmap that will emit updates
    fun getMilestonesForRoadmap(roadmapId: String): Flow<List<Milestone>>

    // Returns a Flow for a specific milestone that will emit updates
    fun getMilestoneById(milestoneId: String): Flow<Milestone?>

    // Methods for modifying data - these should trigger updates in the Flows
    suspend fun updateMilestoneCompletion(milestoneId: String, isCompleted: Boolean)
    suspend fun updateMilestoneNote(milestoneId: String, noteContent: String)

    // Update an existing note
    suspend fun updateExistingNote(noteId: String, newContent: String)

    // Get notes for a specific milestone
    fun getNotesForMilestone(milestoneId: String): Flow<List<Note>>

    // Delete a note by ID
    suspend fun deleteNote(noteId: String)

    // Helper methods
    suspend fun getRoadmapTitle(roadmapId: String): String
}
