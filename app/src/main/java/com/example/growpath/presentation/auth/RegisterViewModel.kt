package com.example.growpath.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.growpath.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    var state by mutableStateOf(AuthState())
        private set

    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events

    fun onEmailChanged(email: String) {
        state = state.copy(email = email)
    }

    fun onPasswordChanged(password: String) {
        state = state.copy(password = password)
    }

    fun onDisplayNameChanged(displayName: String) {
        state = state.copy(displayName = displayName)
    }

    fun onRegisterClick() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            if (!validateInputs()) {
                state = state.copy(isLoading = false)
                return@launch
            }

            registerUseCase(state.email, state.password, state.displayName)
                .onSuccess {
                    state = state.copy(isLoading = false)
                    _events.emit(AuthEvent.RegisterSuccess)
                }
                .onFailure { error ->
                    state = state.copy(
                        isLoading = false,
                        error = error.message ?: "Unknown error occurred"
                    )
                }
        }
    }

    private fun validateInputs(): Boolean {
        if (state.displayName.isBlank()) {
            state = state.copy(error = "Name cannot be empty")
            return false
        }

        if (state.email.isBlank()) {
            state = state.copy(error = "Email cannot be empty")
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            state = state.copy(error = "Invalid email format")
            return false
        }

        if (state.password.isBlank()) {
            state = state.copy(error = "Password cannot be empty")
            return false
        }

        if (state.password.length < 6) {
            state = state.copy(error = "Password must be at least 6 characters")
            return false
        }

        return true
    }
}
