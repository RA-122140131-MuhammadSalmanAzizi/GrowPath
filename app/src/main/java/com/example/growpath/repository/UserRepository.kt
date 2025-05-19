package com.example.growpath.repository

import com.example.growpath.model.Achievement
import com.example.growpath.model.User

interface UserRepository {
    fun getUser(): User?
    // Tambah fungsi lain sesuai kebutuhan

    suspend fun getUserAchievements(): List<Achievement>
    suspend fun updateUserProfile(displayName: String): User
}
