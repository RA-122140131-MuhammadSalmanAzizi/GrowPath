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
import kotlinx.coroutines.flow.catch

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

    private var currentRoadmapId: String? = null

    fun loadMilestones(roadmapId: String) {
        // Store the current roadmap ID to avoid duplicate subscriptions
        if (currentRoadmapId == roadmapId) return
        currentRoadmapId = roadmapId

        _state.update {
            it.copy(isLoading = true, roadmapId = roadmapId, error = null)
        }

        // Launch coroutine to get roadmap title
        viewModelScope.launch {
            try {
                val title = roadmapRepository.getRoadmapTitle(roadmapId)
                _state.update { it.copy(roadmapTitle = title) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to load roadmap title: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }

        // Subscribe to the flow of milestones for this roadmap
        viewModelScope.launch {
            roadmapRepository.getMilestonesForRoadmap(roadmapId)
                .catch { e ->
                    _state.update {
                        it.copy(
                            error = "Failed to load milestones: ${e.message}",
                            isLoading = false
                        )
                    }
                }
                .collect { milestonesList ->
                    _state.update {
                        it.copy(
                            milestones = milestonesList,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun onMilestoneComplete(milestoneId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                // Only need to call the repository method, the Flow will update automatically
                roadmapRepository.updateMilestoneCompletion(milestoneId, isCompleted)

                // We don't need to manually update the local state as the repository will emit updates
                // that will be collected by the Flow collector above
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Failed to update milestone: ${e.message}")
                }
            }
        }
    }

    // Function to trigger a manual refresh if needed
    fun refresh() {
        currentRoadmapId?.let { loadMilestones(it) }
    }
}
