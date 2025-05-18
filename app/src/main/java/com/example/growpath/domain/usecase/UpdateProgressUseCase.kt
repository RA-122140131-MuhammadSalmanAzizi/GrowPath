package com.example.growpath.domain.usecase

import com.example.growpath.data.repository.MilestoneRepository
import com.example.growpath.data.repository.RoadmapRepository
import com.example.growpath.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateProgressUseCase @Inject constructor(
    private val milestoneRepository: MilestoneRepository,
    private val roadmapRepository: RoadmapRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(milestoneId: String, isCompleted: Boolean): Result<Unit> {
        return try {
            // Complete the milestone
            milestoneRepository.completeMilestone(milestoneId, isCompleted)

            // Get milestone to find the roadmap
            val milestone = milestoneRepository.getMilestoneById(milestoneId).first()
            milestone?.let { milestone ->
                // Get all milestones for this roadmap to calculate progress
                val milestones = milestoneRepository.getMilestonesByRoadmapId(milestone.roadmapId).first()

                // Calculate progress percentage
                val totalMilestones = milestones.size
                val completedMilestones = milestones.count { it.isCompleted }
                val progress = if (totalMilestones > 0) completedMilestones.toFloat() / totalMilestones else 0f

                // Update roadmap progress
                roadmapRepository.updateProgress(milestone.roadmapId, progress)

                // If milestone is completed, add experience points to user
                if (isCompleted) {
                    userRepository.getCurrentUserId()?.let { userId ->
                        userRepository.addExperience(userId, milestone.experiencePoints)
                    }
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
