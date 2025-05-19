package com.example.growpath.model

data class User(
    val id: String,
    val displayName: String,
    val email: String,
    val photoUrl: String? = null,
    val level: Int = 1,
    val experience: Int = 0
)
