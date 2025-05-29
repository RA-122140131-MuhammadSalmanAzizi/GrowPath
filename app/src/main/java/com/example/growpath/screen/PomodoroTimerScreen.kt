package com.example.growpath.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroTimerScreen(onBackClick: () -> Unit) {
    var timeLeft by remember { mutableStateOf(25 * 60) } // 25 minutes in seconds
    var isActive by remember { mutableStateOf(false) }
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60

    LaunchedEffect(key1 = timeLeft, key2 = isActive) {
        if (isActive && timeLeft > 0) {
            delay(1000L)
            timeLeft--
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = String.format("%02d:%02d", minutes, seconds),
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { isActive = !isActive },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isActive) "Pause" else if (timeLeft < 25 * 60) "Resume" else "Start")
                }
                Button(
                    onClick = {
                        isActive = false
                        timeLeft = 25 * 60
                    },
                    modifier = Modifier.weight(1f),
                    enabled = timeLeft < 25 * 60
                ) {
                    Text("Reset")
                }
            }
        }
    }
}

