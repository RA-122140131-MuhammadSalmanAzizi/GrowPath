package com.example.growpath.utils

import com.example.growpath.model.Milestone

object GraphUtils {
    fun milestonesByStatus(milestones: List<Milestone>): Pair<List<Milestone>, List<Milestone>> {
        val completed = milestones.filter { it.isCompleted }
        val inProgress = milestones.filter { !it.isCompleted }
        return inProgress to completed
    }
}
