package com.example.growpath.model

data class Roadmap(
    val id: String,
    val title: String,
    val description: String,
    val progress: Float // 0.0 - 1.0
)
