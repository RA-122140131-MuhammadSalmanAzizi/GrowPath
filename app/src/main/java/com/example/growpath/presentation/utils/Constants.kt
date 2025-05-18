package com.example.growpath.presentation.utils

object Constants {
    // Firebase collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_ROADMAPS = "roadmaps"
    const val COLLECTION_MILESTONES = "milestones"
    const val COLLECTION_NOTES = "notes"
    const val COLLECTION_ACHIEVEMENTS = "achievements"

    // Firebase storage paths
    const val STORAGE_PROFILE_IMAGES = "profile_images"
    const val STORAGE_ROADMAP_IMAGES = "roadmap_images"
    const val STORAGE_ACHIEVEMENT_ICONS = "achievement_icons"

    // Shared preferences
    const val PREF_NAME = "growpath_prefs"
    const val PREF_USER_ID = "user_id"
    const val PREF_DARK_MODE = "dark_mode"
    const val PREF_NOTIFICATIONS_ENABLED = "notifications_enabled"

    // Notifications
    const val NOTIFICATION_CHANNEL_ID = "growpath_channel"
    const val NOTIFICATION_CHANNEL_NAME = "GrowPath Notifications"

    // Experience points
    const val XP_PER_LEVEL = 100
    const val XP_MILESTONE_COMPLETION_BASE = 10
    const val XP_ROADMAP_COMPLETION_BONUS = 50

    // Navigation routes
    const val DEEP_LINK_PREFIX = "growpath://"

    // Time constants
    const val ONE_DAY_MILLIS = 86400000L // 24 * 60 * 60 * 1000
}
