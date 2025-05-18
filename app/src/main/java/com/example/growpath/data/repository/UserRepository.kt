package com.example.growpath.data.repository

import com.example.growpath.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<User?>
    fun getUserById(userId: String): Flow<User?>
    suspend fun createUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun addExperience(userId: String, amount: Int)
    suspend fun updateLevel(userId: String, newLevel: Int)
    suspend fun signInWithEmail(email: String, password: String): Result<User>
    suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<User>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    fun signOut()
    fun isUserLoggedIn(): Boolean
    fun getCurrentUserId(): String?
}
