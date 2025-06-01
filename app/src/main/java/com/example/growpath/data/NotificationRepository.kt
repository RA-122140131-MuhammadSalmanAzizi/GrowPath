package com.example.growpath.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.growpath.screen.Notification
import dagger.hilt.android.qualifiers.ApplicationContext
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
class NotificationRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val PREFS_NAME = "notification_prefs"
    private val PREF_READ_NOTIFICATIONS = "read_notifications"

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    // Keep a single source of truth for notifications
    private val _notificationsFlow = MutableStateFlow<List<Notification>>(emptyList())
    val notificationsFlow: StateFlow<List<Notification>> = _notificationsFlow.asStateFlow()

    // Track read status separately for immediate UI updates
    private val _unreadCountFlow = MutableStateFlow(0)
    val unreadCountFlow: StateFlow<Int> = _unreadCountFlow.asStateFlow()

    // Menyimpan set ID notifikasi yang telah dibaca
    private val readNotificationIds: MutableSet<String> = sharedPreferences
        .getStringSet(PREF_READ_NOTIFICATIONS, mutableSetOf<String>())?.toMutableSet()
            ?: mutableSetOf()

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
                isRead = readNotificationIds.contains("module-update-1"), // Cek status dibaca dari SharedPreferences
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
                isRead = readNotificationIds.contains("app-update-1"), // Cek status dibaca dari SharedPreferences
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
                    
                """
            )
            // Tambahkan notifikasi lain sesuai kebutuhan
        )

        _notificationsFlow.value = sampleNotifications

        // Hitung jumlah notifikasi yang belum dibaca berdasarkan data SharedPreferences
        val unreadCount = sampleNotifications.count { !it.isRead }
        _unreadCountFlow.value = unreadCount

        Log.d("NotificationRepository", "Loaded notifications: ${sampleNotifications.size}, unread: $unreadCount")
    }

    fun addNotification(title: String, message: String) {
        val notification = Notification(
            id = System.currentTimeMillis().toString(),
            title = title,
            message = message,
            timestamp = Date(),
            isRead = false
        )

        _notificationsFlow.update { currentList ->
            val updatedList = currentList + notification
            updatedList
        }

        _unreadCountFlow.update { it + 1 }
    }

    fun markAsRead(notificationId: String) {
        // Update notification list
        _notificationsFlow.update { currentList ->
            currentList.map { notification ->
                if (notification.id == notificationId && !notification.isRead) {
                    // Simpan ID notifikasi yang telah dibaca ke SharedPreferences
                    readNotificationIds.add(notificationId)
                    saveReadNotificationIds()

                    // Update unread counter
                    _unreadCountFlow.update { it - 1 }

                    notification.copy(isRead = true)
                } else {
                    notification
                }
            }
        }
    }

    fun markAllAsRead() {
        val currentList = _notificationsFlow.value
        val unreadNotifications = currentList.filter { !it.isRead }

        if (unreadNotifications.isNotEmpty()) {
            // Tambahkan semua ID notifikasi yang belum dibaca ke dalam set
            unreadNotifications.forEach {
                readNotificationIds.add(it.id)
            }

            // Simpan set yang diperbarui ke SharedPreferences
            saveReadNotificationIds()

            // Update notification list
            _notificationsFlow.update { currentList ->
                currentList.map { notification ->
                    notification.copy(isRead = true)
                }
            }

            // Reset unread counter
            _unreadCountFlow.value = 0

            Log.d("NotificationRepository", "Marked all notifications as read")
        }
    }

    fun clearNotifications() {
        _notificationsFlow.value = emptyList()
        _unreadCountFlow.value = 0
    }

    private fun saveReadNotificationIds() {
        sharedPreferences.edit().putStringSet(PREF_READ_NOTIFICATIONS, readNotificationIds).apply()
        Log.d("NotificationRepository", "Saved read notification IDs: $readNotificationIds")
    }

    // Fungsi untuk mengubah semua notifikasi menjadi belum dibaca (untuk debugging)
    fun resetAllReadStatus() {
        readNotificationIds.clear()
        saveReadNotificationIds()
        loadSampleNotifications()
    }
}
