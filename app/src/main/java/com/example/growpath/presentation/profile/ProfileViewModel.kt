package com.example.growpath.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.growpath.domain.usecase.GetAchievementsUseCase
import com.example.growpath.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getAchievementsUseCase: GetAchievementsUseCase
) : ViewModel() {

    var state by mutableStateOf(ProfileState())
        private set

    init {
        loadUserProfile()
        loadAchievements()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                getUserProfileUseCase().collectLatest { user ->
                    state = state.copy(
                        user = user,
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

    private fun loadAchievements() {
        viewModelScope.launch {
            try {
                getAchievementsUseCase().collectLatest { achievements ->
                    state = state.copy(
                        achievements = achievements,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                state = state.copy(
                    error = e.message ?: "Failed to load achievements",
                    isLoading = false
                )
            }
        }
    }

    fun onLogout() {
        // This would be handled by a user repository/use case
    }

    fun onRefresh() {
        loadUserProfile()
        loadAchievements()
    }
}
