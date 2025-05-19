package com.example.growpath.screen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

// Simple data class for a notification
data class Notification(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

class NotificationsViewModel : ViewModel() {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    fun addNotification(title: String, message: String) {
        _notifications.value = listOf(
            Notification(title = title, message = message)
        ) + _notifications.value
    }

    fun clearNotifications() {
        _notifications.value = emptyList()
    }
}

