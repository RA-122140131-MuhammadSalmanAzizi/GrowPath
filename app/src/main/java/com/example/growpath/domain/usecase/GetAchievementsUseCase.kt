package com.example.growpath.domain.usecase

import com.example.growpath.data.model.Achievement
import com.example.growpath.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class GetAchievementsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<List<Achievement>> {
        // In a real implementation, this would query achievements from a repository
        // For now, returning empty flow as a placeholder
        return emptyFlow()
    }
}
