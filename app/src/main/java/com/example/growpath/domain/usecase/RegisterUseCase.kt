package com.example.growpath.domain.usecase

import com.example.growpath.data.model.User
import com.example.growpath.data.repository.UserRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String, displayName: String): Result<User> {
        return userRepository.signUpWithEmail(email, password, displayName)
    }
}
