package com.example.growpath.presentation.dashboard

import com.example.growpath.data.model.Roadmap

data class DashboardState(
    val userName: String = "",
    val userLevel: Int = 1,
    val userExperience: Int = 0,
    val inProgressRoadmaps: List<Roadmap> = emptyList(),
    val completedRoadmaps: List<Roadmap> = emptyList(),
    val notStartedRoadmaps: List<Roadmap> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
