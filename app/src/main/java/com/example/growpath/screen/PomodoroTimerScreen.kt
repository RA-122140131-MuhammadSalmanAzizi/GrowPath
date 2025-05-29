package com.example.growpath.screen

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.growpath.service.PomodoroService
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroTimerScreen(onBackClick: () -> Unit) {
    // Context for service operations
    val context = LocalContext.current

    // Timer state
    var timeLeft by remember { mutableStateOf(25 * 60) } // default: 25 minutes in seconds
    var isActive by remember { mutableStateOf(false) }
    var isWorkPhase by remember { mutableStateOf(true) }

    // Customizable timer lengths (in minutes)
    var workLength by remember { mutableStateOf("25") }
    var breakLength by remember { mutableStateOf("5") }

    // Settings dialog state
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Service connection
    var pomodoroService by remember { mutableStateOf<PomodoroService?>(null) }
    var bound by remember { mutableStateOf(false) }

    // Background colors for different phases
    val workBackgroundColor = Color(0xFF2196F3) // Blue for focus time
    val breakBackgroundColor = Color(0xFF9C27B0) // Purple for break time

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60

    // Service connection callback
    val connection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val binder = service as PomodoroService.LocalBinder
                pomodoroService = binder.getService()
                bound = true

                // Initialize with service values
                timeLeft = pomodoroService?.getTimeLeft() ?: timeLeft
                isActive = pomodoroService?.isActive() ?: isActive
                isWorkPhase = pomodoroService?.isWorkPhase() ?: isWorkPhase
                workLength = (pomodoroService?.getWorkDuration() ?: 25).toString()
                breakLength = (pomodoroService?.getBreakDuration() ?: 5).toString()

                // Register listener for updates
                pomodoroService?.addListener(object : PomodoroService.PomodoroListener {
                    override fun onTimerTick(timeLeftSeconds: Int) {
                        timeLeft = timeLeftSeconds
                    }

                    override fun onPhaseChanged(isWork: Boolean) {
                        isWorkPhase = isWork
                    }

                    override fun onTimerStatusChanged(isRunning: Boolean) {
                        isActive = isRunning
                    }
                })
            }

            override fun onServiceDisconnected(name: ComponentName) {
                bound = false
                pomodoroService = null
            }
        }
    }

    // Bind to the service when the screen is shown
    DisposableEffect(context) {
        val intent = Intent(context, PomodoroService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        context.startService(intent)

        onDispose {
            if (bound) {
                context.unbindService(connection)
                bound = false
            }
        }
    }

    // Helper function to reset timer with appropriate duration based on phase
    fun resetTimer(isWork: Boolean) {
        pomodoroService?.resetTimer(isWork)
    }

    // Helper function to start/pause the timer
    fun toggleTimer() {
        pomodoroService?.toggleTimer()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isWorkPhase) "Pomodoro Timer - Focus" else "Pomodoro Timer - Break") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Settings button
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(Icons.Default.Timer, contentDescription = "Timer Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(if (isWorkPhase) workBackgroundColor else breakBackgroundColor)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Current phase text
            Text(
                text = if (isWorkPhase) "Focus Time" else "Break Time",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display current settings
            Text(
                text = "Focus: $workLength min | Break: $breakLength min",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Timer display
            Card(
                modifier = Modifier
                    .size(200.dp),
                shape = RoundedCornerShape(100.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isWorkPhase) workBackgroundColor else breakBackgroundColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Timer controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { toggleTimer() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = if (isWorkPhase) workBackgroundColor else breakBackgroundColor
                    )
                ) {
                    Text(if (isActive) "Pause" else "Start")
                }

                Button(
                    onClick = { resetTimer(isWorkPhase) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = if (isWorkPhase) workBackgroundColor else breakBackgroundColor
                    )
                ) {
                    Text("Reset")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Switch between work and break
            Button(
                onClick = {
                    resetTimer(!isWorkPhase)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = if (!isWorkPhase) workBackgroundColor else breakBackgroundColor
                )
            ) {
                Text(if (isWorkPhase) "Switch to Break" else "Switch to Focus")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Timer will continue in background",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }

    // Settings Dialog
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Custom Timer Settings") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Work duration input
                    OutlinedTextField(
                        value = workLength,
                        onValueChange = { workLength = it },
                        label = { Text("Focus Duration (minutes)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    // Break duration input
                    OutlinedTextField(
                        value = breakLength,
                        onValueChange = { breakLength = it },
                        label = { Text("Break Duration (minutes)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Validate and convert inputs
                        val workTime = workLength.toIntOrNull() ?: 25
                        val breakTime = breakLength.toIntOrNull() ?: 5

                        // Update with valid values
                        workLength = workTime.toString()
                        breakLength = breakTime.toString()

                        // Update service settings
                        pomodoroService?.setWorkDuration(workTime)
                        pomodoroService?.setBreakDuration(breakTime)

                        // Reset timer with new duration if not active
                        if (!isActive) {
                            resetTimer(isWorkPhase)
                        }

                        showSettingsDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSettingsDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
