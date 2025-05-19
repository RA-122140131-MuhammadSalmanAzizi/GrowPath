package com.example.growpath.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.growpath.model.Roadmap
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class DashboardEvent {
    data class RoadmapCompleted(val title: String, val message: String) : DashboardEvent()
}

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

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val roadmapRepository: RoadmapRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private var previousCompletedIds: Set<String> = emptySet()
    private var roadmapJob: kotlinx.coroutines.Job? = null

    // Event for communicating with other ViewModels
    private val _events = MutableSharedFlow<DashboardEvent>()
    val events = _events.asSharedFlow()

    init {
        observeRoadmaps()
    }

    private fun observeRoadmaps() {
        roadmapJob?.cancel()
        roadmapJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            roadmapRepository.getRoadmaps()
                .catch { e ->
                    _state.update {
                        it.copy(
                            error = "Failed to load roadmaps: ${e.message}",
                            isLoading = false
                        )
                    }
                }
                .collect { roadmapList ->
                    val newCompleted = roadmapList.filter { roadmap -> roadmap.progress >= 1.0f }
                    val newCompletedIds = newCompleted.map { it.id }.toSet()
                    val justCompleted = newCompleted.filter { it.id !in previousCompletedIds }

                    // Use the event emission instead of direct ViewModel call
                    if (justCompleted.isNotEmpty()) {
                        notifyRoadmapsCompleted(justCompleted)
                    }
                    previousCompletedIds = newCompletedIds

                    _state.update {
                        it.copy(
                            inProgressRoadmaps = roadmapList.filter { roadmap -> roadmap.progress > 0f && roadmap.progress < 1.0f },
                            completedRoadmaps = newCompleted,
                            notStartedRoadmaps = roadmapList.filter { roadmap -> roadmap.progress == 0f },
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    // Modified to emit an event instead of directly calling NotificationsViewModel
    private suspend fun notifyRoadmapsCompleted(justCompleted: List<Roadmap>) {
        justCompleted.forEach { roadmap ->
            _events.emit(DashboardEvent.RoadmapCompleted(
                title = "Congrats!",
                message = "You have completed the roadmap: ${roadmap.title}"
            ))
        }
    }

    fun onRefresh() {
        observeRoadmaps()
    }

    fun onCreateRoadmapClick() {
        // Logika untuk membuat roadmap baru nanti bisa ditambahkan di sini
        // Sebagai contoh, kita bisa menampilkan dialog atau navigasi ke screen pembuatan roadmap
    }
}
