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

data class MilestoneState(
    val milestone: Milestone? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MilestoneViewModel @Inject constructor(private val roadmapRepository: RoadmapRepository) : ViewModel() {
    private val _state = MutableStateFlow(MilestoneState())
    val state: StateFlow<MilestoneState> = _state.asStateFlow()

    fun loadMilestone(milestoneId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                // Ambil data milestone dari repository
                val milestone = roadmapRepository.getMilestoneById(milestoneId)
                _state.update {
                    it.copy(
                        milestone = milestone,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to load milestone: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleMilestoneCompletion(milestoneId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                // Update di repository
                roadmapRepository.updateMilestoneCompletion(milestoneId, isCompleted)

                // Update state lokal
                _state.update { currentState ->
                    currentState.milestone?.let {
                        if (it.id == milestoneId) {
                            currentState.copy(milestone = it.copy(isCompleted = isCompleted))
                        } else {
                            currentState
                        }
                    } ?: currentState
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Failed to update milestone: ${e.message}")
                }
            }
        }
    }

    fun updateMilestoneNote(milestoneId: String, noteContent: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // Panggil metode repository untuk update catatan
                roadmapRepository.updateMilestoneNote(milestoneId, noteContent)

                // Update state lokal
                _state.update { currentState ->
                    currentState.milestone?.let {
                        if (it.id == milestoneId) {
                            currentState.copy(
                                milestone = it.copy(note = noteContent),
                                isLoading = false
                            )
                        } else {
                            currentState.copy(isLoading = false)
                        }
                    } ?: currentState.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to update note: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
}
