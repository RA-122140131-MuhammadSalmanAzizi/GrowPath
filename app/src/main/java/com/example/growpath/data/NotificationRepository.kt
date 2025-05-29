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
            Notification(
                title = "Welcome to GrowPath!",
                message = "Start exploring roadmaps to begin your learning journey."
            ),
            Notification(
                title = "New Android Development Roadmap",
                message = "Check out the new Android Development roadmap with Jetpack Compose."
            ),
            Notification(
                title = "Daily Reminder",
                message = "Don't forget to check your progress on Kotlin Multiplatform roadmap."
            )
        )

        _notificationsFlow.value = sampleNotifications
        updateUnreadCount()
    }

    fun markAsRead(notificationId: String) {
        val updatedNotifications = _notificationsFlow.value.map { notification ->
            if (notification.id == notificationId) {
                notification.copy(isRead = true)
            } else {
                notification
            }
        }
        _notificationsFlow.value = updatedNotifications
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
