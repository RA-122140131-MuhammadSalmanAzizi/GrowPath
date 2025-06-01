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
    val error: String? = null,
    val completedToday: Int = 0,   // Number of milestones completed today
    val currentStreak: Int = 0     // Current daily streak of learning
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

    // Constants for statistics tracking
    companion object {
        private const val KEY_LAST_ACTIVE_DATE = "last_active_date"
        private const val KEY_CURRENT_STREAK = "current_streak"
        private const val KEY_TODAY_COMPLETIONS = "today_completions"
        private const val KEY_COMPLETED_MILESTONES = "completed_milestones"
        private const val KEY_LAST_COMPLETED_DATE = "last_completed_date" // Track when user last completed a milestone
        private const val KEY_COMPLETED_TODAY = "completed_milestone_today" // Track if user completed milestone today
    }

    init {
        loadFavorites()
        loadStatistics() // Load saved statistics
        updateDailyStreak() // Update streak when app opens
        observeUser()
        observeRoadmaps()
        observeLastOpenedRoadmap()
        observeMilestoneCompletions() // Track milestone completions
    }

    // Load favorites from SharedPreferences
    private fun loadFavorites() {
        // Get current username to create a user-specific key
        val username = userRepository.getCurrentUsername() ?: return
        val userSpecificKey = "${username}_favorite_roadmaps"

        val favoritesJson = sharedPreferences.getString(userSpecificKey, null)
        if (favoritesJson != null) {
            val type = object : TypeToken<List<Roadmap>>() {}.type
            try {
                val favorites = gson.fromJson<List<Roadmap>>(favoritesJson, type)
                _state.update { it.copy(favoriteRoadmaps = favorites) }
            } catch (e: Exception) {
                // Handle potential parsing errors
                sharedPreferences.edit().remove(userSpecificKey).apply()
            }
        }
    }

    // Save favorites to SharedPreferences
    private fun saveFavorites(favorites: List<Roadmap>) {
        // Get current username to create a user-specific key
        val username = userRepository.getCurrentUsername() ?: return
        val userSpecificKey = "${username}_favorite_roadmaps"

        val favoritesJson = gson.toJson(favorites)
        sharedPreferences.edit().putString(userSpecificKey, favoritesJson).apply()
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

    // === Statistics tracking functionality ===

    // Track milestone completions to update statistics
    private fun observeMilestoneCompletions() {
        viewModelScope.launch {
            try {
                // Get username for user-specific data
                val username = userRepository.getCurrentUsername() ?: return@launch
                val userSpecificKey = "${username}_$KEY_COMPLETED_MILESTONES"

                // Load already completed milestone IDs
                val completedMilestoneIdsSet = sharedPreferences.getStringSet(userSpecificKey, setOf()) ?: setOf()
                val trackedCompletedMilestones = completedMilestoneIdsSet.toMutableSet()

                // We'll track all roadmaps and their milestones
                val allRoadmaps = roadmapRepository.getRoadmaps()
                allRoadmaps.collect { roadmaps ->
                    roadmaps.forEach { roadmap ->
                        viewModelScope.launch {
                            roadmapRepository.getMilestonesForRoadmap(roadmap.id).collect { milestones ->
                                var milestonesCompletedNow = 0

                                // Find newly completed milestones
                                milestones.forEach { milestone ->
                                    if (milestone.isCompleted && !trackedCompletedMilestones.contains(milestone.id)) {
                                        // This is a newly completed milestone
                                        trackedCompletedMilestones.add(milestone.id)
                                        milestonesCompletedNow++

                                        // When a milestone is completed, mark today as having a completion
                                        markMilestoneCompletedToday()

                                        // Update achievement progress
                                        updateAchievementProgress()
                                    }
                                }

                                if (milestonesCompletedNow > 0) {
                                    // Save the updated set of tracked milestones
                                    sharedPreferences.edit()
                                        .putStringSet(userSpecificKey, trackedCompletedMilestones)
                                        .apply()

                                    // Update today's completion count (add the newly completed ones)
                                    val newTodayTotal = _state.value.completedToday + milestonesCompletedNow
                                    saveCompletedToday(newTodayTotal)

                                    // Update UI state
                                    _state.update { it.copy(completedToday = newTodayTotal) }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle errors
                _state.update { it.copy(error = "Failed to track completions: ${e.message}") }
            }
        }
    }

    // Mark that a milestone was completed today (for streak tracking)
    private fun markMilestoneCompletedToday() {
        val username = userRepository.getCurrentUsername() ?: return

        // Get current date as string (format: YYYY-MM-DD)
        val today = java.time.LocalDate.now().toString()

        // Create user-specific keys
        val lastCompletedDateKey = "${username}_$KEY_LAST_COMPLETED_DATE"
        val completedTodayKey = "${username}_$KEY_COMPLETED_TODAY"
        val streakKey = "${username}_$KEY_CURRENT_STREAK"

        // Check if this is the first milestone completed today
        val alreadyCompletedToday = sharedPreferences.getBoolean(completedTodayKey, false)

        // If this is the first milestone today, increment streak
        if (!alreadyCompletedToday) {
            // Get current streak
            val currentStreak = sharedPreferences.getInt(streakKey, 0)

            // Calculate new streak value
            val newStreak = currentStreak + 1

            // Save updated streak
            sharedPreferences.edit().putInt(streakKey, newStreak).apply()

            // Update UI
            _state.update { it.copy(currentStreak = newStreak) }
        }

        // Save that user completed a milestone today
        sharedPreferences.edit()
            .putString(lastCompletedDateKey, today)
            .putBoolean(completedTodayKey, true)
            .apply()
    }

    // Update the daily data when the app is opened (different day check)
    private fun updateDailyStreak() {
        val username = userRepository.getCurrentUsername() ?: return

        // Create user-specific keys
        val lastActiveKey = "${username}_$KEY_LAST_ACTIVE_DATE"
        val lastCompletedDateKey = "${username}_$KEY_LAST_COMPLETED_DATE"
        val streakKey = "${username}_$KEY_CURRENT_STREAK"
        val todayCompletionsKey = "${username}_$KEY_TODAY_COMPLETIONS"
        val completedTodayKey = "${username}_$KEY_COMPLETED_TODAY"

        // Get current date
        val today = java.time.LocalDate.now()
        val todayString = today.toString()

        // Get last active date
        val lastActiveDate = sharedPreferences.getString(lastActiveKey, null)

        // Check if we've already checked today
        if (lastActiveDate == todayString) {
            // Already updated today, nothing more to do
            return
        }

        // Get last completed date
        val lastCompletedDateString = sharedPreferences.getString(lastCompletedDateKey, null)

        if (lastCompletedDateString != null) {
            val lastCompletedDate = java.time.LocalDate.parse(lastCompletedDateString)
            val yesterday = today.minusDays(1)

            // Check if user missed a full day (no milestone completions)
            if (lastCompletedDate.isBefore(yesterday)) {
                // Reset streak to 0 since user missed at least one full day
                sharedPreferences.edit().putInt(streakKey, 0).apply()
                _state.update { it.copy(currentStreak = 0) }
            }
        }

        // It's a new day! Reset the "completed today" counter and flag for the new day
        sharedPreferences.edit()
            .putString(lastActiveKey, todayString)
            .putInt(todayCompletionsKey, 0)
            .putBoolean(completedTodayKey, false)
            .apply()

        // Update UI state with zeroed completions for the new day
        _state.update { it.copy(completedToday = 0) }
    }

    // Load previously saved statistics
    private fun loadStatistics() {
        val username = userRepository.getCurrentUsername() ?: return

        // Create user-specific keys
        val streakKey = "${username}_$KEY_CURRENT_STREAK"
        val todayCompletionsKey = "${username}_$KEY_TODAY_COMPLETIONS"

        // Get saved values with defaults
        val streak = sharedPreferences.getInt(streakKey, 0)
        val completedToday = sharedPreferences.getInt(todayCompletionsKey, 0)

        // Update state with loaded values
        _state.update { it.copy(
            currentStreak = streak,
            completedToday = completedToday
        )}
    }

    // Save the count of milestones completed today
    private fun saveCompletedToday(count: Int) {
        val username = userRepository.getCurrentUsername() ?: return
        val todayCompletionsKey = "${username}_$KEY_TODAY_COMPLETIONS"
        sharedPreferences.edit().putInt(todayCompletionsKey, count).apply()
    }

    // Update achievement progress for Knowledge Explorer and Learning Journey
    private fun updateAchievementProgress() {
        val username = userRepository.getCurrentUsername() ?: return

        try {
            // Get achievement repository from user repository
            val achievementRepository = userRepository.getAchievementRepository()
            achievementRepository?.let { repo ->
                viewModelScope.launch {
                    // Knowledge Explorer: Track distinct roadmaps with progress
                    val roadmapsWithProgress = mutableSetOf<String>()

                    roadmapRepository.getRoadmaps().collect { allRoadmaps ->
                        // First identify all roadmaps with progress
                        for (roadmap in allRoadmaps) {
                            // Check if this roadmap has any progress
                            if (roadmap.progress > 0) {
                                roadmapsWithProgress.add(roadmap.id)
                            }
                        }

                        // Knowledge Explorer: Check if exactly 3 different roadmaps are started (not less)
                        // Only record achievement when the count equals or exceeds 3
                        if (roadmapsWithProgress.size >= 3) {
                            // Record all roadmaps that have been started (for Knowledge Explorer)
                            for (roadmapId in roadmapsWithProgress) {
                                repo.recordRoadmapStarted(roadmapId)
                            }
                        }

                        // Learning Journey: Based on completing 2 roadmaps fully
                        for (roadmap in allRoadmaps) {
                            // Check for completed roadmaps
                            if (roadmap.progress >= 1.0f) {
                                // Record that this roadmap was completed
                                repo.recordRoadmapCompletion(roadmap.id)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Handle errors
            _state.update { it.copy(error = "Failed to update achievements: ${e.message}") }
        }
    }
}
