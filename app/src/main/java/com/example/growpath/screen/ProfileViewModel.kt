package com.example.growpath.screen

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
        // Subscribe to user flow for real-time updates
        viewModelScope.launch {
            userRepository.getUserFlow().collect { user ->
                _state.update { it.copy(user = user) }
            }
        }

        loadAchievements()
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
                userRepository.updateUserProfile(displayName)
                // No need to update state manually as the Flow will handle it
                _state.update { it.copy(isLoading = false) }
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

    fun updateProfilePhoto(photoUrl: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                userRepository.updateUserPhoto(photoUrl)
                // No need to update state manually as the Flow will handle it
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to update profile photo: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    // Function to logout user
    fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                // Dummy logout implementation
                // In a real app, this would call a logout method in UserRepository
                // that would clear tokens, sessions, etc.

                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to logout: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
}
