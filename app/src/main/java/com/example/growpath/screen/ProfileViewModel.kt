package com.example.growpath.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.growpath.model.Achievement
import com.example.growpath.model.User
import com.example.growpath.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
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

@HiltViewModel
class ProfileViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfile()
        loadAchievements()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val user = userRepository.getUser()
                _state.update {
                    it.copy(user = user, isLoading = false)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to load profile: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            _state.update { it.copy(isAchievementsLoading = true) }
            try {
                val achievements = userRepository.getUserAchievements()
                _state.update {
                    it.copy(
                        achievements = achievements,
                        isAchievementsLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to load achievements: ${e.message}",
                        isAchievementsLoading = false
                    )
                }
            }
        }
    }

    fun updateProfile(displayName: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val updatedUser = userRepository.updateUserProfile(displayName)
                _state.update { state ->
                    state.copy(user = updatedUser, isLoading = false)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to update profile: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
}
