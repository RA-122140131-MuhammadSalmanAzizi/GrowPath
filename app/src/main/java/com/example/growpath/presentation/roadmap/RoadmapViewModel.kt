package com.example.growpath.presentation.roadmap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.growpath.domain.usecase.GetMilestonesUseCase
import com.example.growpath.domain.usecase.UpdateProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoadmapViewModel @Inject constructor(
    private val getMilestonesUseCase: GetMilestonesUseCase,
    private val updateProgressUseCase: UpdateProgressUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var state by mutableStateOf(RoadmapState())
        private set

    init {
        savedStateHandle.get<String>("roadmapId")?.let { roadmapId ->
            loadRoadmapData(roadmapId)
        }
    }

    private fun loadRoadmapData(roadmapId: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                getMilestonesUseCase(roadmapId).collectLatest { milestones ->
                    state = state.copy(
                        milestones = milestones,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                state = state.copy(
                    error = e.message ?: "Failed to load roadmap data",
                    isLoading = false
                )
            }
        }
    }

    fun onMilestoneClick(milestoneId: String) {
        // Navigation will be handled by the composable
    }

    fun onMilestoneComplete(milestoneId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                updateProgressUseCase(milestoneId, isCompleted)
                    .onFailure { error ->
                        state = state.copy(error = error.message ?: "Failed to update milestone")
                    }
            } catch (e: Exception) {
                state = state.copy(error = e.message ?: "Failed to update milestone")
            }
        }
    }

    fun onRefresh() {
        savedStateHandle.get<String>("roadmapId")?.let { roadmapId ->
            loadRoadmapData(roadmapId)
        }
    }
}
