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
        val currentTime = System.currentTimeMillis()
        val day = 24 * 60 * 60 * 1000L // One day in milliseconds

        val sampleNotifications = listOf(
            // Module update notification with detailed content
            Notification(
                id = "module-update-1",
                title = "New Learning Modules Available",
                message = "Check out our latest learning modules with practical exercises and real-world examples",
                timestamp = Date(currentTime),
                isRead = false,
                detailedContent = """
                    # New Learning Modules Available
                    
                    We're excited to announce several new learning modules that have been added to the GrowPath platform:
                    
                    ## Advanced GraphQL Integration
                    
                    This comprehensive module covers GraphQL implementation with practical exercises for:
                    - Setting up GraphQL servers with Apollo
                    - Building efficient queries and mutations
                    - Real-time data with GraphQL subscriptions
                    - Authentication and authorization in GraphQL APIs
                    
                    ## Machine Learning Fundamentals
                    
                    Learn the basics of machine learning with hands-on TensorFlow exercises:
                    - Data preprocessing and cleaning
                    - Building and training simple ML models
                    - Model evaluation and improvement
                    - Deploying ML models to production
                    
                    ## Cybersecurity Essentials
                    
                    Master the fundamentals of keeping applications secure:
                    - Common web security vulnerabilities
                    - Secure authentication implementation 
                    - Data encryption best practices
                    - Security testing methodologies
                    
                    Start learning today by browsing the Explore section!
                """
            ),

            // App update notification with detailed content
            Notification(
                id = "app-update-1",
                title = "GrowPath App Update",
                message = "We've fixed bugs and added new features to improve your learning experience",
                timestamp = Date(currentTime - 3 * day),
                isRead = false,
                detailedContent = """
                    # GrowPath App Update (v2.4.1)
                    
                    We're constantly working to improve your GrowPath experience. This update includes several bug fixes and new features:
                    
                    ## Bug Fixes
                    
                    - Fixed progress tracking issues in some learning paths
                    - Resolved login issues some users were experiencing
                    - Fixed screen flickering on some Android 12+ devices
                    - Improved app stability and reduced crashes
                    
                    ## New Features
                    
                    - **Favorites System**: You can now mark roadmaps as favorites for quick access
                    - **Improved UI**: Cleaner interface with better visibility and contrast
                    - **Offline Mode**: Continue learning even without an internet connection
                    - **Performance Optimizations**: Faster loading times and smoother animations
                    
                    ## Coming Soon
                    
                    - Group learning features
                    - Custom learning path creation
                    - Advanced analytics for tracking your progress
                    
                    Thanks for using GrowPath! We appreciate your feedback as we continue to improve.
                """
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
