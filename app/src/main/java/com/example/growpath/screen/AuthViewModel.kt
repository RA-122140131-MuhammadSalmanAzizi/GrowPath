package com.example.growpath.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class AuthState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
    var state by mutableStateOf(AuthState())
        private set

    fun onEmailChange(newEmail: String) {
        state = state.copy(email = newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        state = state.copy(password = newPassword)
    }

    fun login(onSuccess: () -> Unit) {
        state = state.copy(isLoading = true, error = null)
        // Simulasi login, ganti dengan logic autentikasi real sesuai kebutuhan
        if (state.email == "salman@example.com" && state.password == "123456") {
            state = state.copy(isLoading = false, isSuccess = true)
            onSuccess()
        } else {
            state = state.copy(isLoading = false, error = "Invalid email or password")
        }
    }
}
