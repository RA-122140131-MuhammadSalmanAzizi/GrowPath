package com.example.growpath.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.growpath.model.Milestone
import com.example.growpath.model.Note
import com.example.growpath.repository.RoadmapRepository
import com.example.growpath.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.catch

data class MilestoneState(
    val milestone: Milestone? = null,
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCompletionAnimation: Boolean = false
)

@HiltViewModel
class MilestoneViewModel @Inject constructor(
    private val roadmapRepository: RoadmapRepository,
    private val userRepository: UserRepository // Injeksi UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(MilestoneState())
    val state: StateFlow<MilestoneState> = _state.asStateFlow()

    private var currentMilestoneId: String? = null

    fun loadMilestone(milestoneId: String) {
        // Avoid duplicate subscriptions
        if (currentMilestoneId == milestoneId) return
        currentMilestoneId = milestoneId

        _state.update { it.copy(isLoading = true, error = null) }

        // Subscribe to the Flow for this milestone
        viewModelScope.launch {
            roadmapRepository.getMilestoneById(milestoneId)
                .catch { e ->
                    _state.update {
                        it.copy(
                            error = "Failed to load milestone: ${e.message}",
                            isLoading = false
                        )
                    }
                }
                .collect { milestone ->
                    _state.update {
                        it.copy(
                            milestone = milestone,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }

        // Load notes for this milestone
        viewModelScope.launch {
            roadmapRepository.getNotesForMilestone(milestoneId)
                .catch { e ->
                    _state.update {
                        it.copy(
                            error = "Failed to load notes: ${e.message}",
                            isLoading = false
                        )
                    }
                }
                .collect { notes ->
                    _state.update {
                        it.copy(
                            notes = notes,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun toggleMilestoneCompletion(milestoneId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                // If completing a milestone, show the animation
                if (isCompleted) {
                    _state.update { it.copy(showCompletionAnimation = true) }
                    userRepository.addExperiencePoints(125) // Menambahkan reward 125 XP
                }

                // Only need to call the repository method, the Flow will update automatically
                roadmapRepository.updateMilestoneCompletion(milestoneId, isCompleted)

                // No need to manually update state - the Flow collector will receive the update
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Failed to update milestone: ${e.message}")
                }
            }
        }
    }

    fun dismissCompletionAnimation() {
        _state.update { it.copy(showCompletionAnimation = false) }
    }

    fun updateMilestoneNote(milestoneId: String, noteContent: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // Call repository method
                roadmapRepository.updateMilestoneNote(milestoneId, noteContent)

                // No need to manually update state - the Flow collector will receive the update
                _state.update { it.copy(isLoading = false) }
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

    fun updateExistingNote(noteId: String, newContent: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // Call repository method to update the existing note
                roadmapRepository.updateExistingNote(noteId, newContent)

                // No need to manually update state - the Flow collector will receive the update
                _state.update { it.copy(isLoading = false) }
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

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // Call repository method to delete the note
                roadmapRepository.deleteNote(noteId)

                // No need to manually update state - the Flow collector will receive the update
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to delete note: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
}
