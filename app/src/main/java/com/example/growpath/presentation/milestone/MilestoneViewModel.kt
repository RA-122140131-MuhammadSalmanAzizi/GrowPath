package com.example.growpath.presentation.milestone

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.growpath.domain.usecase.AddNoteUseCase
import com.example.growpath.domain.usecase.UpdateProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MilestoneViewModel @Inject constructor(
    private val updateProgressUseCase: UpdateProgressUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var state by mutableStateOf(MilestoneState())
        private set

    init {
        savedStateHandle.get<String>("milestoneId")?.let { milestoneId ->
            loadMilestoneData(milestoneId)
        }
    }

    private fun loadMilestoneData(milestoneId: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                // In a real implementation, this would fetch the milestone and notes from a repository
                // For now, setting isLoading to false as a placeholder
                state = state.copy(isLoading = false)
            } catch (e: Exception) {
                state = state.copy(
                    error = e.message ?: "Failed to load milestone data",
                    isLoading = false
                )
            }
        }
    }

    fun onCompleteToggle(isCompleted: Boolean) {
        viewModelScope.launch {
            state.milestone?.id?.let { milestoneId ->
                try {
                    updateProgressUseCase(milestoneId, isCompleted)
                        .onSuccess {
                            // Update local state with completion status
                            state.milestone?.let { milestone ->
                                state = state.copy(
                                    milestone = milestone.copy(
                                        isCompleted = isCompleted,
                                        completedAt = if (isCompleted) System.currentTimeMillis() else null
                                    )
                                )
                            }
                        }
                        .onFailure { error ->
                            state = state.copy(error = error.message ?: "Failed to update milestone")
                        }
                } catch (e: Exception) {
                    state = state.copy(error = e.message ?: "Failed to update milestone")
                }
            }
        }
    }

    fun onNoteContentChanged(content: String) {
        state = state.copy(newNoteContent = content)
    }

    fun onAddNoteClick() {
        state = state.copy(isNoteDialogVisible = true)
    }

    fun onDismissNoteDialog() {
        state = state.copy(isNoteDialogVisible = false, newNoteContent = "")
    }

    fun onSaveNote() {
        if (state.newNoteContent.isBlank()) {
            state = state.copy(error = "Note content cannot be empty")
            return
        }

        viewModelScope.launch {
            state.milestone?.id?.let { milestoneId ->
                try {
                    addNoteUseCase(milestoneId, state.newNoteContent)
                        .onSuccess {
                            state = state.copy(
                                isNoteDialogVisible = false,
                                newNoteContent = ""
                            )
                            // Refresh notes
                            loadMilestoneData(milestoneId)
                        }
                        .onFailure { error ->
                            state = state.copy(error = error.message ?: "Failed to save note")
                        }
                } catch (e: Exception) {
                    state = state.copy(error = e.message ?: "Failed to save note")
                }
            }
        }
    }

    fun onRefresh() {
        savedStateHandle.get<String>("milestoneId")?.let { milestoneId ->
            loadMilestoneData(milestoneId)
        }
    }
}
