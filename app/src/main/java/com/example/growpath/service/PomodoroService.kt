package com.example.growpath.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.growpath.R
import kotlinx.coroutines.*

class PomodoroService : Service() {
    private val binder = LocalBinder()
    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    // Timer state
    private var timeLeft: Int = 25 * 60
    private var isActive: Boolean = false
    private var isWorkPhase: Boolean = true

    // Timer settings
    private var workDuration: Int = 25 * 60
    private var breakDuration: Int = 5 * 60

    // Notification IDs
    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "pomodoro_channel"
        private const val ACTION_STOP = "com.example.growpath.STOP"
        private const val ACTION_TOGGLE = "com.example.growpath.TOGGLE"
    }

    // Listeners
    private val listeners = mutableListOf<PomodoroListener>()

    inner class LocalBinder : Binder() {
        fun getService(): PomodoroService = this@PomodoroService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopTimer()
                stopSelf()
            }
            ACTION_TOGGLE -> {
                toggleTimer()
            }
        }

        startForeground(NOTIFICATION_ID, createNotification())
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        scope.cancel()
    }

    // Create an alert notification channel with high importance
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Regular channel for the ongoing timer
            val defaultChannel = NotificationChannel(
                CHANNEL_ID,
                "Pomodoro Timer",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Ongoing Pomodoro Timer notifications"
            }

            // Separate channel for pop-up alerts with high importance
            val alertChannel = NotificationChannel(
                "pomodoro_alert_channel",
                "Pomodoro Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Pomodoro timer alerts for phase changes"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500, 250, 500)
                setBypassDnd(true)
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(defaultChannel)
            notificationManager.createNotificationChannel(alertChannel)
        }
    }

    private fun createNotification(): Notification {
        val phaseText = if (isWorkPhase) "Focus Time" else "Break Time"
        val minutes = timeLeft / 60
        val seconds = timeLeft % 60
        val timeText = String.format("%02d:%02d", minutes, seconds)

        val stopIntent = Intent(this, PomodoroService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val toggleIntent = Intent(this, PomodoroService::class.java).apply {
            action = ACTION_TOGGLE
        }
        val togglePendingIntent = PendingIntent.getService(
            this, 1, toggleIntent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Pomodoro Timer - $phaseText")
            .setContentText("Time remaining: $timeText")
            .setSmallIcon(R.drawable.ic_timer)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(
                R.drawable.ic_pause,
                if (isActive) "Pause" else "Start",
                togglePendingIntent
            )
            .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
            .setOngoing(true)
            .build()
    }

    // Start or pause the timer
    fun toggleTimer() {
        isActive = !isActive

        if (isActive) {
            startTimer()
        } else {
            timerJob?.cancel()
        }

        // Update notification
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(NOTIFICATION_ID, createNotification())

        // Notify listeners
        notifyTimerStatusChanged()
    }

    // Start the timer countdown
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (isActive && timeLeft > 0) {
                delay(1000)
                timeLeft--

                // Update notification every 5 seconds to reduce system load
                if (timeLeft % 5 == 0 || timeLeft <= 5) {
                    val notificationManager = NotificationManagerCompat.from(this@PomodoroService)
                    notificationManager.notify(NOTIFICATION_ID, createNotification())
                }

                // Notify listeners every second
                notifyTimerUpdate()

                // When timer reaches 0
                if (timeLeft <= 0) {
                    switchPhase()
                }
            }
        }
    }

    // Reset the timer
    fun resetTimer(isWork: Boolean = isWorkPhase) {
        isWorkPhase = isWork
        timeLeft = if (isWork) workDuration else breakDuration

        // Update notification
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(NOTIFICATION_ID, createNotification())

        // Notify listeners
        notifyTimerUpdate()
        notifyPhaseChanged()
    }

    // Stop the timer completely
    fun stopTimer() {
        isActive = false
        timerJob?.cancel()
    }

    // Set the work duration (in seconds)
    fun setWorkDuration(minutes: Int) {
        workDuration = minutes * 60
        if (isWorkPhase) {
            timeLeft = workDuration
            notifyTimerUpdate()
        }
    }

    // Set the break duration (in seconds)
    fun setBreakDuration(minutes: Int) {
        breakDuration = minutes * 60
        if (!isWorkPhase) {
            timeLeft = breakDuration
            notifyTimerUpdate()
        }
    }

    // Get current timer state
    fun getTimeLeft(): Int = timeLeft
    fun isActive(): Boolean = isActive
    fun isWorkPhase(): Boolean = isWorkPhase
    fun getWorkDuration(): Int = workDuration / 60
    fun getBreakDuration(): Int = breakDuration / 60

    // Add listener for timer updates
    fun addListener(listener: PomodoroListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    // Remove listener
    fun removeListener(listener: PomodoroListener) {
        listeners.remove(listener)
    }

    // Notify all listeners about timer update
    private fun notifyTimerUpdate() {
        listeners.forEach { it.onTimerTick(timeLeft) }
    }

    // Notify all listeners about phase change
    private fun notifyPhaseChanged() {
        listeners.forEach { it.onPhaseChanged(isWorkPhase) }
    }

    // Notify all listeners about timer status change (start/pause)
    private fun notifyTimerStatusChanged() {
        listeners.forEach { it.onTimerStatusChanged(isActive) }
    }

    // Interface for timer updates
    interface PomodoroListener {
        fun onTimerTick(timeLeft: Int) {}
        fun onPhaseChanged(isWorkPhase: Boolean) {}
        fun onTimerStatusChanged(isActive: Boolean) {}
    }

    // Handle phase change with pop-up notification
    private fun switchPhase() {
        // Switch the phase and reset timer
        isWorkPhase = !isWorkPhase
        timeLeft = if (isWorkPhase) workDuration else breakDuration

        // Message content based on phase
        val phaseText = if (isWorkPhase) "Focus Time" else "Break Time"
        val messageText = if (isWorkPhase)
            "Time to focus! ${workDuration/60} minute focus session started."
        else
            "Time for a ${breakDuration/60} minute break! Relax and refresh."

        // Create intent to launch app when notification is clicked
        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("pomodoro_notification", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create heads-up notification
        val notificationBuilder = NotificationCompat.Builder(this, "pomodoro_alert_channel")
            .setContentTitle("Pomodoro Timer - $phaseText")
            .setContentText(messageText)
            .setSmallIcon(R.drawable.ic_timer)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true) // Heads-up notification
            .setAutoCancel(true)
            .setLights(Color.BLUE, 1000, 500)
            .setDefaults(NotificationCompat.DEFAULT_SOUND) // Add sound

        // Show the notification
        try {
            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(NOTIFICATION_ID + 1, notificationBuilder.build())

            // Also update the ongoing notification
            notificationManager.notify(NOTIFICATION_ID, createNotification())
        } catch (e: Exception) {
            // Fail silently if notification cannot be shown
        }

        // Notify listeners
        notifyPhaseChanged()
    }
}

