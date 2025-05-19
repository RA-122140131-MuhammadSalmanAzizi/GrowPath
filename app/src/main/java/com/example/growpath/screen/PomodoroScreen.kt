package com.example.growpath.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(onBackClick: () -> Unit) {
    var timeLeft by remember { mutableStateOf(25 * 60) } // 25 minutes in seconds
    var isActive by remember { mutableStateOf(false) }
    var isBreak by remember { mutableStateOf(false) }

    LaunchedEffect(isActive, timeLeft) {
        if (isActive && timeLeft > 0) {
            delay(1000)
            timeLeft--
        } else if (timeLeft == 0) {
            isActive = false
            // TODO: Add sound notification or vibration
            if (!isBreak) {
                // Start break
                isBreak = true
                timeLeft = 5 * 60 // 5 minutes break
            } else {
                // Start new session
                isBreak = false
                timeLeft = 25 * 60 // 25 minutes session
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pomodoro Timer") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isBreak) "Break Time!" else "Study Session",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = formatTime(timeLeft),
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = { isActive = !isActive }) {
                    Text(if (isActive) "Pause" else if (timeLeft == 0 || (isBreak && timeLeft == 5*60) || (!isBreak && timeLeft == 25*60) ) "Start" else "Resume")
                }
                Button(
                    onClick = {
                        isActive = false
                        isBreak = false
                        timeLeft = 25 * 60
                    },
                    enabled = timeLeft < 25 * 60 || isBreak
                ) {
                    Text("Reset")
                }
            }
        }
    }
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}

