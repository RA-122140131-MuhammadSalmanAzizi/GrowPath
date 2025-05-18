package com.example.growpath.domain.usecase

import com.example.growpath.data.model.Milestone
import com.example.growpath.data.repository.MilestoneRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMilestonesUseCase @Inject constructor(
    private val milestoneRepository: MilestoneRepository
) {
    operator fun invoke(roadmapId: String): Flow<List<Milestone>> {
        return milestoneRepository.getMilestonesByRoadmapId(roadmapId)
    }
}
