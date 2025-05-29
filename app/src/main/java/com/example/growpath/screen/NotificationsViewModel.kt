package com.example.growpath.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.growpath.data.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
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

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsState())
    val state: StateFlow<NotificationsState> = _state.asStateFlow()

    init {
        // Combine flows from repository to update the UI state
        combine(
            notificationRepository.notificationsFlow,
            notificationRepository.unreadCountFlow
        ) { notifications, unreadCount ->
            _state.update { currentState ->
                currentState.copy(
                    notifications = notifications,
                    unreadCount = unreadCount
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Unit
        )
    }

    fun addNotification(title: String, message: String) {
        notificationRepository.addNotification(title, message)
    }

    fun markAsRead(notificationId: String) {
        notificationRepository.markAsRead(notificationId)
    }

    fun markAllAsRead() {
        notificationRepository.markAllAsRead()
    }

    fun clearNotifications() {
        notificationRepository.clearNotifications()
    }
}
