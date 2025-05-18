package com.example.growpath.domain.usecase

import com.example.growpath.data.model.User
import com.example.growpath.data.repository.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return userRepository.signInWithEmail(email, password)
    }
}
