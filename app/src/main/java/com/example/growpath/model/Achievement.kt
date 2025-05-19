package com.example.growpath.model

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconUrl: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)
