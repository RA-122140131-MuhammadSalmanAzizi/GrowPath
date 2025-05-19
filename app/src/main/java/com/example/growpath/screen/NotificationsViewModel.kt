package com.example.growpath.screen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Date

data class Notification(
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val message: String,
    val timestamp: Date = Date(),
    val isRead: Boolean = false
)

data class NotificationsState(
    val notifications: List<Notification> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class NotificationsViewModel : ViewModel() {

    private val _state = MutableStateFlow(NotificationsState())
    val state: StateFlow<NotificationsState> = _state.asStateFlow()

    init {
        // Load sample notifications for testing
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

        _state.update { currentState ->
            currentState.copy(
                notifications = sampleNotifications,
                unreadCount = sampleNotifications.count { !it.isRead }
            )
        }
    }

    fun addNotification(title: String, message: String) {
        val newNotification = Notification(
            title = title,
            message = message
        )

        _state.update { currentState ->
            val updatedNotifications = currentState.notifications + newNotification
            currentState.copy(
                notifications = updatedNotifications,
                unreadCount = updatedNotifications.count { !it.isRead }
            )
        }
    }

    fun markAsRead(notificationId: String) {
        _state.update { currentState ->
            val updatedNotifications = currentState.notifications.map { notification ->
                if (notification.id == notificationId) {
                    notification.copy(isRead = true)
                } else {
                    notification
                }
            }

            currentState.copy(
                notifications = updatedNotifications,
                unreadCount = updatedNotifications.count { !it.isRead }
            )
        }
    }

    fun markAllAsRead() {
        _state.update { currentState ->
            val updatedNotifications = currentState.notifications.map { it.copy(isRead = true) }
            currentState.copy(
                notifications = updatedNotifications,
                unreadCount = 0
            )
        }
    }

    fun clearNotifications() {
        _state.update {
            it.copy(notifications = emptyList(), unreadCount = 0)
        }
    }
}
