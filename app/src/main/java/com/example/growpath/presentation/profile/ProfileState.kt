package com.example.growpath.presentation.profile

import com.example.growpath.data.model.Achievement
import com.example.growpath.data.model.User

data class ProfileState(
    val user: User? = null,
    val achievements: List<Achievement> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
