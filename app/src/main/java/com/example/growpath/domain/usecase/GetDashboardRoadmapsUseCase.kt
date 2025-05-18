package com.example.growpath.domain.usecase

import com.example.growpath.data.model.Roadmap
import com.example.growpath.data.repository.RoadmapRepository
import com.example.growpath.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDashboardRoadmapsUseCase @Inject constructor(
    private val roadmapRepository: RoadmapRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<List<Roadmap>> {
        val userId = userRepository.getCurrentUserId() ?: return emptyFlow()
        return roadmapRepository.getRoadmapsByUserId(userId)
    }
}
