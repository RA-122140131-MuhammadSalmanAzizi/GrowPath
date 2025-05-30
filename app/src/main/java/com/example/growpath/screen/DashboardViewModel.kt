package com.example.growpath.screen

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.growpath.model.Roadmap
import com.example.growpath.model.Milestone
import com.example.growpath.repository.RoadmapRepository
import com.example.growpath.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

sealed class DashboardEvent {
    data class RoadmapCompleted(val title: String, val message: String) : DashboardEvent()
}

data class DashboardState(
    val userName: String = "User",
    val userLevel: Int = 1,
    val userExperience: Int = 0,
    val inProgressRoadmaps: List<Roadmap> = emptyList(),
    val completedRoadmaps: List<Roadmap> = emptyList(),
    val notStartedRoadmaps: List<Roadmap> = emptyList(),
    val favoriteRoadmaps: List<Roadmap> = emptyList(),
    val lastOpenedRoadmap: Roadmap? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val roadmapRepository: RoadmapRepository,
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private var previousCompletedIds: Set<String> = emptySet()
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("growpath_preferences", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Event for communicating with other ViewModels
    private val _events = MutableSharedFlow<DashboardEvent>()
    val events = _events.asSharedFlow()

    init {
        loadFavorites()
        observeUser()
        observeRoadmaps()
        observeLastOpenedRoadmap()
    }

    // Load favorites from SharedPreferences
    private fun loadFavorites() {
        val favoritesJson = sharedPreferences.getString("favorite_roadmaps", null)
        if (favoritesJson != null) {
            val type = object : TypeToken<List<Roadmap>>() {}.type
            try {
                val favorites = gson.fromJson<List<Roadmap>>(favoritesJson, type)
                _state.update { it.copy(favoriteRoadmaps = favorites) }
            } catch (e: Exception) {
                // Handle potential parsing errors
                sharedPreferences.edit().remove("favorite_roadmaps").apply()
            }
        }
    }

    // Save favorites to SharedPreferences
    private fun saveFavorites(favorites: List<Roadmap>) {
        val favoritesJson = gson.toJson(favorites)
        sharedPreferences.edit().putString("favorite_roadmaps", favoritesJson).apply()
    }

    private fun observeUser() {
        viewModelScope.launch {
            userRepository.getUserFlow().collect { user ->
                user?.let {
                    _state.update { currentState ->
                        currentState.copy(
                            userName = it.displayName,
                            userLevel = it.level,
                            userExperience = it.experience
                        )
                    }
                }
            }
        }
    }

    private fun observeRoadmaps() {
        viewModelScope.launch {
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

    private fun observeLastOpenedRoadmap() {
        viewModelScope.launch {
            roadmapRepository.getLastOpenedRoadmap()
                .catch { e ->
                    _state.update {
                        it.copy(error = "Failed to load last roadmap: ${e.message}")
                    }
                }
                .collect { roadmap ->
                    _state.update {
                        it.copy(lastOpenedRoadmap = roadmap)
                    }
                }
        }
    }

    // Metode untuk digunakan ketika pengguna mengklik roadmap
    fun onRoadmapClick(roadmapId: String) {
        viewModelScope.launch {
            try {
                // Simpan roadmap yang diklik sebagai roadmap terakhir dibuka
                roadmapRepository.markRoadmapAsLastOpened(roadmapId)
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Failed to update last roadmap: ${e.message}")
                }
            }
        }
    }

    // Manual refresh function that can be triggered by pull-to-refresh
    fun refresh() {
        // No need to explicitly re-fetch data since we're using a continuous Flow
        // Just update the loading state to show a refresh indicator
        _state.update { it.copy(isLoading = true) }
        // The loading state will be automatically updated when new data arrives
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

    // Add a roadmap to favorites
    fun addToFavorites(roadmap: Roadmap) {
        _state.update { currentState ->
            val currentFavorites = currentState.favoriteRoadmaps.toMutableList()
            if (!currentFavorites.any { it.id == roadmap.id }) {
                currentFavorites.add(roadmap)
            }
            saveFavorites(currentFavorites) // Save to SharedPreferences
            currentState.copy(favoriteRoadmaps = currentFavorites)
        }
    }

    // Remove a roadmap from favorites
    fun removeFromFavorites(roadmapId: String) {
        _state.update { currentState ->
            val currentFavorites = currentState.favoriteRoadmaps.toMutableList()
            currentFavorites.removeAll { it.id == roadmapId }
            saveFavorites(currentFavorites) // Save to SharedPreferences
            currentState.copy(favoriteRoadmaps = currentFavorites)
        }
    }

    // Check if a roadmap is in favorites
    fun isRoadmapFavorite(roadmapId: String): Boolean {
        return _state.value.favoriteRoadmaps.any { it.id == roadmapId }
    }

    // Toggle favorite status of a roadmap
    fun toggleFavorite(roadmap: Roadmap) {
        if (isRoadmapFavorite(roadmap.id)) {
            removeFromFavorites(roadmap.id)
        } else {
            addToFavorites(roadmap)
        }
    }
}
