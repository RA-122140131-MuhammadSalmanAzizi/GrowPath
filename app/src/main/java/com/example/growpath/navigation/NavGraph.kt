package com.example.growpath.navigation

object NavGraph {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val PROFILE = "profile"
    const val ROADMAP = "roadmap/{roadmapId}"
    const val MILESTONE = "milestone/{milestoneId}"
    const val ACHIEVEMENTS = "achievements"
    const val EXPLORE = "explore"
    const val POMODORO = "pomodoro"
    const val NOTIFICATIONS = "notifications"
    const val NOTIFICATION_DETAIL = "notification_detail/{notificationId}"
    const val ABOUT = "about"
    const val POMODORO_TIMER = "pomodoro_timer"
    const val ACCOUNT_SETTINGS = "account_settings"

    fun roadmapWithId(roadmapId: String): String = "roadmap/$roadmapId"
    fun milestoneWithId(milestoneId: String): String = "milestone/$milestoneId"
    fun notificationDetailWithId(notificationId: String): String = "notification_detail/$notificationId"
}
