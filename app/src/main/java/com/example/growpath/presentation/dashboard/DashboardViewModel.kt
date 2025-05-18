package com.example.growpath.presentation.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.growpath.data.model.Roadmap
import com.example.growpath.domain.usecase.GetDashboardRoadmapsUseCase
import com.example.growpath.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardRoadmapsUseCase: GetDashboardRoadmapsUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    var state by mutableStateOf(DashboardState())
        private set

    init {
        loadUserProfile()
        loadRoadmaps()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                getUserProfileUseCase().collectLatest { user ->
                    state = state.copy(
                        userName = user?.displayName ?: "",
                        userLevel = user?.level ?: 1,
                        userExperience = user?.experience ?: 0,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                state = state.copy(
                    error = e.message ?: "Failed to load user profile",
                    isLoading = false
                )
            }
        }
    }

    private fun loadRoadmaps() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                getDashboardRoadmapsUseCase().collectLatest { roadmaps ->
                    val inProgressRoadmaps = roadmaps.filter { it.progress > 0 && it.progress < 1f }
                    val completedRoadmaps = roadmaps.filter { it.progress >= 1f }
                    val notStartedRoadmaps = roadmaps.filter { it.progress == 0f }

                    state = state.copy(
                        inProgressRoadmaps = inProgressRoadmaps,
                        completedRoadmaps = completedRoadmaps,
                        notStartedRoadmaps = notStartedRoadmaps,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                state = state.copy(
                    error = e.message ?: "Failed to load roadmaps",
                    isLoading = false
                )
            }
        }
    }

    fun onRoadmapClick(roadmapId: String) {
        // Handle roadmap click, navigation will be done by the composable
    }

    fun onCreateRoadmapClick() {
        // This would navigate to create roadmap screen
    }

    fun onRefresh() {
        loadRoadmaps()
    }
}
