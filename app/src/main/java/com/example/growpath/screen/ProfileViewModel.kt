package com.example.growpath.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.growpath.model.Achievement
import com.example.growpath.model.User
import com.example.growpath.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileState(
    val user: User? = null,
    val achievements: List<Achievement> = emptyList(),
    val isLoading: Boolean = false,
    val isAchievementsLoading: Boolean = false,
    val error: String? = null
)

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfile()
        loadAchievements()
    }

    private fun loadProfile() {
        _state.update { it.copy(isLoading = true) }

        val user = userRepository.getUser()
        _state.update {
            it.copy(
                user = user,
                isLoading = false,
                error = if (user == null) "Failed to load user profile" else null
            )
        }
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            _state.update { it.copy(isAchievementsLoading = true) }
            try {
                val achievements = userRepository.getUserAchievements()
                _state.update { it.copy(
                    achievements = achievements,
                    isAchievementsLoading = false
                )}
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = "Failed to load achievements: ${e.message}",
                    isAchievementsLoading = false
                )}
            }
        }
    }

    fun updateProfile(displayName: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val updatedUser = userRepository.updateUserProfile(displayName)
                _state.update { it.copy(
                    user = updatedUser,
                    isLoading = false,
                    error = null
                )}
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = "Failed to update profile: ${e.message}",
                    isLoading = false
                )}
            }
        }
    }
}
