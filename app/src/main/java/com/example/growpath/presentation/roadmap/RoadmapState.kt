package com.example.growpath.presentation.roadmap

import com.example.growpath.data.model.Milestone
import com.example.growpath.data.model.Roadmap

data class RoadmapState(
    val roadmap: Roadmap? = null,
    val milestones: List<Milestone> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
