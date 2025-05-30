package com.example.growpath.data

import com.example.growpath.screen.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton repository that manages notifications across the app
 */
@Singleton
class NotificationRepository @Inject constructor() {

    // Keep a single source of truth for notifications
    private val _notificationsFlow = MutableStateFlow<List<Notification>>(emptyList())
    val notificationsFlow: StateFlow<List<Notification>> = _notificationsFlow.asStateFlow()

    // Track read status separately for immediate UI updates
    private val _unreadCountFlow = MutableStateFlow(0)
    val unreadCountFlow: StateFlow<Int> = _unreadCountFlow.asStateFlow()

    init {
        // Initialize with sample notifications
        loadSampleNotifications()
    }

    private fun loadSampleNotifications() {
        val sampleNotifications = listOf(
            // Welcome & Onboarding Notifications
            Notification(
                title = "Welcome to GrowPath!",
                message = "Start exploring roadmaps to begin your learning journey.",
                timestamp = Date(System.currentTimeMillis() - 3600000) // 1 hour ago
            ),
            Notification(
                title = "Thank you for joining GrowPath!",
                message = "We're excited to help you achieve your learning goals. Explore our roadmaps to get started.",
                timestamp = Date(System.currentTimeMillis() - 7200000) // 2 hours ago
            ),
            Notification(
                title = "Complete your profile",
                message = "Set your learning preferences to get personalized roadmap recommendations.",
                timestamp = Date(System.currentTimeMillis() - 86400000) // 1 day ago
            ),

            // System Maintenance Notifications
            Notification(
                title = "Scheduled Maintenance",
                message = "GrowPath will be undergoing maintenance on June 5, 2025, from 2:00-4:00 AM UTC. Brief service interruptions may occur.",
                timestamp = Date(System.currentTimeMillis() - 172800000) // 2 days ago
            ),
            Notification(
                title = "Maintenance Complete",
                message = "System maintenance has been completed successfully. New features are now available!",
                timestamp = Date(System.currentTimeMillis() - 259200000) // 3 days ago
            ),
            Notification(
                title = "Server Optimization",
                message = "We've optimized our servers for better performance. Enjoy faster roadmap loading times!",
                timestamp = Date(System.currentTimeMillis() - 432000000) // 5 days ago
            ),

            // App Update Notifications
            Notification(
                title = "GrowPath v2.5.0 Available",
                message = "Update to the latest version for new features including offline roadmap access and improved milestone tracking.",
                timestamp = Date(System.currentTimeMillis() - 345600000) // 4 days ago
            ),
            Notification(
                title = "Security Update Available",
                message = "We've enhanced security in our latest update. Please update your app to stay protected.",
                timestamp = Date(System.currentTimeMillis() - 518400000) // 6 days ago
            ),
            Notification(
                title = "What's New in v2.5.0",
                message = "Discover dark mode, custom roadmap creation, and progress analytics in our latest update!",
                timestamp = Date(System.currentTimeMillis() - 345600000) // 4 days ago
            ),

            // Content Notifications
            Notification(
                title = "New Android Development Roadmap",
                message = "Check out the new Android Development roadmap with Jetpack Compose.",
                timestamp = Date(System.currentTimeMillis() - 604800000) // 7 days ago
            ),
            Notification(
                title = "Daily Reminder",
                message = "Don't forget to check your progress on Kotlin Multiplatform roadmap.",
                timestamp = Date(System.currentTimeMillis() - 691200000) // 8 days ago
            )
        )

        _notificationsFlow.value = sampleNotifications
        updateUnreadCount()
    }

    fun markAsRead(notificationId: String) {
        // Print debug info to see which notification ID we're marking as read
        println("Marking notification as read: $notificationId")

        val updatedNotifications = _notificationsFlow.value.map { notification ->
            if (notification.id == notificationId) {
                println("Found matching notification: ${notification.title}")
                notification.copy(isRead = true)
            } else {
                // Keep other notifications unchanged
                notification
            }
        }
        _notificationsFlow.value = updatedNotifications
        updateUnreadCount()
    }

    /**
     * Sends a notification when a user completes all milestones in a roadmap
     */
    fun sendRoadmapCompletedNotification(roadmapId: String) {
        val notification = Notification(
            title = "Roadmap Completed!",
            message = "Congratulations! You've completed all milestones in a roadmap.",
            timestamp = Date()
        )

        _notificationsFlow.update { currentList ->
            val newList = currentList.toMutableList()
            newList.add(0, notification) // Add at the beginning of the list
            newList
        }

        updateUnreadCount()
    }

    fun markAllAsRead() {
        val readNotifications = _notificationsFlow.value.map { it.copy(isRead = true) }
        _notificationsFlow.value = readNotifications
        _unreadCountFlow.value = 0
    }

    fun clearNotifications() {
        _notificationsFlow.value = emptyList()
        _unreadCountFlow.value = 0
    }

    fun addNotification(title: String, message: String) {
        val newNotification = Notification(
            title = title,
            message = message,
            timestamp = Date()
        )
        _notificationsFlow.update { currentList -> currentList + newNotification }
        updateUnreadCount()
    }

    private fun updateUnreadCount() {
        _unreadCountFlow.value = _notificationsFlow.value.count { !it.isRead }
    }
}
