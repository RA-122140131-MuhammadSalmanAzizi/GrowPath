package com.example.growpath.screen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.growpath.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository
) : ViewModel() {

    // UI state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Mendapatkan context aplikasi
    fun getContext(): Context = context

    // Get current username
    fun getCurrentUsername(): String? {
        return userRepository.getCurrentUsername()
    }

    // Login with stored credentials
    fun login(username: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val success = userRepository.login(username, password)
                if (success) {
                    onSuccess()
                } else {
                    _errorMessage.value = "Invalid username or password"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Login failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Change username
    fun changeUsername(oldUsername: String, password: String, newUsername: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val success = userRepository.changeUsername(oldUsername, password, newUsername)
                if (success) {
                    onSuccess()
                } else {
                    _errorMessage.value = "Failed to change username. Please verify your credentials."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Change password
    fun changePassword(username: String, oldPassword: String, newPassword: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val success = userRepository.changePassword(username, oldPassword, newPassword)
                if (success) {
                    onSuccess()
                } else {
                    _errorMessage.value = "Failed to change password. Please verify your credentials."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Register new user
    fun register(username: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val success = userRepository.register(username, password)
                if (success) {
                    // Auto login after successful registration
                    userRepository.login(username, password)
                    onSuccess()
                } else {
                    _errorMessage.value = "Username already exists. Please try another username."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Registration failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Clear error message
    fun clearError() {
        _errorMessage.value = null
    }
}
