package com.example.growpath.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.growpath.model.Milestone
import com.example.growpath.repository.RoadmapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RoadmapState(
    val roadmapId: String = "",
    val roadmapTitle: String = "Roadmap",
    val milestones: List<Milestone> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class RoadmapViewModel @Inject constructor(private val roadmapRepository: RoadmapRepository) : ViewModel() {
    private val _state = MutableStateFlow(RoadmapState())
    val state: StateFlow<RoadmapState> = _state.asStateFlow()

    fun loadMilestones(roadmapId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true, roadmapId = roadmapId, error = null)
            }
            try {
                // Ambil data dari repository
                val title = roadmapRepository.getRoadmapTitle(roadmapId)
                val milestonesList = roadmapRepository.getMilestonesForRoadmap(roadmapId)

                _state.update {
                    it.copy(
                        roadmapTitle = title,
                        milestones = milestonesList,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to load roadmap details: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onMilestoneComplete(milestoneId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                // Update completion status in the repository
                roadmapRepository.updateMilestoneCompletion(milestoneId, isCompleted)

                // Update the local state
                _state.update { currentState ->
                    currentState.copy(
                        milestones = currentState.milestones.map { milestone ->
                            if (milestone.id == milestoneId) {
                                milestone.copy(isCompleted = isCompleted)
                            } else {
                                milestone
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Failed to update milestone: ${e.message}")
                }
            }
        }
    }
}
