package com.example.growpath.data.repository

import com.example.growpath.data.local.MilestoneDao
import com.example.growpath.data.local.NoteDao
import com.example.growpath.data.model.Milestone
import com.example.growpath.data.model.Note
import com.example.growpath.data.remote.FirestoreService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MilestoneRepositoryImpl @Inject constructor(
    private val milestoneDao: MilestoneDao,
    private val noteDao: NoteDao,
    private val firestoreService: FirestoreService
) : MilestoneRepository {

    override fun getMilestonesByRoadmapId(roadmapId: String): Flow<List<Milestone>> {
        return milestoneDao.getMilestonesByRoadmapId(roadmapId)
    }

    override fun getMilestoneById(milestoneId: String): Flow<Milestone?> {
        return milestoneDao.getMilestoneById(milestoneId)
    }

    override suspend fun createMilestone(milestone: Milestone) {
        milestoneDao.insertMilestone(milestone)
        firestoreService.createMilestone(milestone)
    }

    override suspend fun updateMilestone(milestone: Milestone) {
        milestoneDao.updateMilestone(milestone)
        firestoreService.updateMilestone(milestone)
    }

    override suspend fun deleteMilestone(milestoneId: String) {
        val milestone = milestoneDao.getMilestoneById(milestoneId).first()
        milestone?.let {
            milestoneDao.deleteMilestone(it)
            firestoreService.deleteMilestone(milestoneId)
        }
    }

    override suspend fun completeMilestone(milestoneId: String, isCompleted: Boolean) {
        val completedAt = if (isCompleted) System.currentTimeMillis() else null
        milestoneDao.updateMilestoneCompletion(milestoneId, isCompleted, completedAt)

        // Update in Firestore
        val milestone = milestoneDao.getMilestoneById(milestoneId).first()
        milestone?.let { firestoreService.updateMilestone(it) }
    }

    // Note operations
    override fun getNotesByMilestoneId(milestoneId: String): Flow<List<Note>> {
        return noteDao.getNotesByMilestoneId(milestoneId)
    }

    override suspend fun createNote(note: Note) {
        noteDao.insertNote(note)
        firestoreService.createNote(note)
    }

    override suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
        firestoreService.updateNote(note)
    }

    override suspend fun deleteNote(noteId: String) {
        val note = noteDao.getNoteById(noteId).first()
        note?.let {
            noteDao.deleteNote(it)
            firestoreService.deleteNote(noteId)
        }
    }
}
