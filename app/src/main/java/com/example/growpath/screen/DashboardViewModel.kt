package com.example.growpath.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.growpath.model.Roadmap
import com.example.growpath.model.Milestone
import com.example.growpath.repository.RoadmapRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardState(
    val userName: String = "Salman",
    val userLevel: Int = 1,
    val userExperience: Int = 32,
    val inProgressRoadmaps: List<Roadmap> = emptyList(),
    val completedRoadmaps: List<Roadmap> = emptyList(),
    val notStartedRoadmaps: List<Roadmap> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class DashboardViewModel(
    private val roadmapRepository: RoadmapRepository,
    private val notificationsViewModel: NotificationsViewModel? = null // Optional injection
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private var previousCompletedIds: Set<String> = emptySet()

    init {
        loadRoadmaps()
    }

    private fun loadRoadmaps() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val roadmapList = roadmapRepository.getRoadmaps()
                val newCompleted = roadmapList.filter { roadmap -> roadmap.progress >= 1f }
                val newCompletedIds = newCompleted.map { it.id }.toSet()
                val justCompleted = newCompleted.filter { it.id !in previousCompletedIds }
                // Trigger notification for each newly completed roadmap
                justCompleted.forEach { roadmap ->
                    notificationsViewModel?.addNotification(
                        title = "Congrats!",
                        message = "You have completed the roadmap: ${roadmap.title}"
                    )
                }
                previousCompletedIds = newCompletedIds
                _state.update {
                    it.copy(
                        inProgressRoadmaps = roadmapList.filter { roadmap -> roadmap.progress in 0.01f..0.99f },
                        completedRoadmaps = newCompleted,
                        notStartedRoadmaps = roadmapList.filter { roadmap -> roadmap.progress == 0f },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to load roadmaps: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onRefresh() {
        loadRoadmaps()
    }

    fun onCreateRoadmapClick() {
        // Logika untuk membuat roadmap baru nanti bisa ditambahkan di sini
        // Sebagai contoh, kita bisa menampilkan dialog atau navigasi ke screen pembuatan roadmap
    }
}
