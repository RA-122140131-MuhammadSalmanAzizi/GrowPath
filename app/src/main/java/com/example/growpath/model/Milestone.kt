package com.example.growpath.model

data class Milestone(
    val id: String,
    val roadmapId: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val note: String? = null,
    val youtubeUrl: String? = null,
    val documentationUrl: String? = null
)
