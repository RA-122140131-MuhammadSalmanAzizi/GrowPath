package com.example.growpath.navigation

object NavGraph {
    const val DASHBOARD = "dashboard"
    const val PROFILE = "profile"
    const val ROADMAP = "roadmap/{roadmapId}"
    const val MILESTONE = "milestone/{milestoneId}"
    const val ACHIEVEMENTS = "achievements"
    const val EXPLORE = "explore"
    const val POMODORO = "pomodoro" // New route for Pomodoro screen
    const val NOTIFICATIONS = "notifications"
    const val ABOUT = "about"

    fun roadmapWithId(roadmapId: String): String = "roadmap/$roadmapId"
    fun milestoneWithId(milestoneId: String): String = "milestone/$milestoneId"
}
