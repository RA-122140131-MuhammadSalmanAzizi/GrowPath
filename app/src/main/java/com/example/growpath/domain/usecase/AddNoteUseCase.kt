package com.example.growpath.domain.usecase

import com.example.growpath.data.model.Note
import com.example.growpath.data.repository.MilestoneRepository
import java.util.UUID
import javax.inject.Inject

class AddNoteUseCase @Inject constructor(
    private val milestoneRepository: MilestoneRepository
) {
    suspend operator fun invoke(milestoneId: String, content: String): Result<Unit> {
        return try {
            val note = Note(
                id = UUID.randomUUID().toString(),
                milestoneId = milestoneId,
                content = content,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            milestoneRepository.createNote(note)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
