package com.example.growpath.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.growpath.theme.Primary
import com.example.growpath.theme.Secondary
import com.example.growpath.theme.Background
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(onBackClick: () -> Unit) {
    var sessionDuration by remember { mutableStateOf(25) } // in minutes
    var breakDuration by remember { mutableStateOf(5) } // in minutes
    var timeLeft by remember { mutableStateOf(sessionDuration * 60) }
    var isBreak by remember { mutableStateOf(false) }
    var isActive by remember { mutableStateOf(false) }
    var showDurationDialog by remember { mutableStateOf(false) }
    var isEditingSession by remember { mutableStateOf(true) }

    // Define color scheme that matches the app theme
    val focusColor = Primary
    val breakColor = Secondary
    val currentColor = if (isBreak) breakColor else focusColor
    val backgroundColor = Background

    // Timer logic
    LaunchedEffect(isActive, isBreak, timeLeft, sessionDuration, breakDuration) {
        if ((isActive && !isBreak) || isBreak) {
            if (timeLeft > 0) {
                delay(1000)
                timeLeft--
            } else {
                if (!isBreak) {
                    // Auto start break
                    isBreak = true
                    timeLeft = breakDuration * 60
                } else {
                    // End break, wait for user to start session again
                    isBreak = false
                    isActive = false
                    timeLeft = sessionDuration * 60
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pomodoro Timer", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = currentColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Status chip - minimalist indicator
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = currentColor.copy(alpha = 0.15f),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isBreak) Icons.Default.Coffee else Icons.Default.Timer,
                        contentDescription = null,
                        tint = currentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBreak) "Break Time" else "Focus Time",
                        style = MaterialTheme.typography.labelLarge,
                        color = currentColor
                    )
                }
            }

            // Timer display - clean, prominent and larger
            Box(
                modifier = Modifier.padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = currentColor.copy(alpha = 0.1f),
                    modifier = Modifier.size(260.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Surface(
                            shape = CircleShape,
                            color = currentColor.copy(alpha = 0.2f),
                            modifier = Modifier.size(220.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Surface(
                                    shape = CircleShape,
                                    color = currentColor,
                                    modifier = Modifier.size(180.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = formatTime(timeLeft),
                                            style = MaterialTheme.typography.headlineLarge.copy(
                                                fontSize = 42.sp
                                            ),
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (isBreak) "Break" else "Focus",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Duration settings - simplified with single dialog
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Focus duration
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        isEditingSession = true
                        showDurationDialog = true
                    }
                ) {
                    Text(
                        text = "Focus",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = "$sessionDuration min",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = focusColor
                    )
                }

                // Break duration
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        isEditingSession = false
                        showDurationDialog = true
                    }
                ) {
                    Text(
                        text = "Break",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = "$breakDuration min",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = breakColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Control buttons - equal sized, simplified
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Start Button
                Button(
                    onClick = { isActive = !isActive },
                    enabled = !isBreak,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = currentColor,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        if (isActive && !isBreak) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (isActive && !isBreak) "Pause" else "Start",
                        fontWeight = FontWeight.Medium
                    )
                }

                // Reset Button - now same size as Start button
                Button(
                    onClick = {
                        isBreak = false
                        isActive = false
                        timeLeft = sessionDuration * 60
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = currentColor.copy(alpha = 0.15f),
                        contentColor = currentColor
                    )
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Reset",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // Duration selection dialog
    if (showDurationDialog) {
        val durationOptions = (1..60).toList()
        val title = if (isEditingSession) "Focus Duration" else "Break Duration"
        val currentValue = if (isEditingSession) sessionDuration else breakDuration
        val color = if (isEditingSession) focusColor else breakColor

        AlertDialog(
            onDismissRequest = { showDurationDialog = false },
            title = { Text(title) },
            containerColor = Color.White,
            titleContentColor = color,
            text = {
                LazyColumn(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(durationOptions) { minutes ->
                        val isSelected = minutes == currentValue

                        Surface(
                            onClick = {
                                if (isEditingSession) {
                                    sessionDuration = minutes
                                    if (!isBreak && !isActive) {
                                        timeLeft = sessionDuration * 60
                                    }
                                } else {
                                    breakDuration = minutes
                                    if (isBreak) {
                                        timeLeft = breakDuration * 60
                                    }
                                }
                                showDurationDialog = false
                            },
                            color = if (isSelected) color.copy(alpha = 0.1f) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "$minutes min",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) color else Color.DarkGray,
                                modifier = Modifier
                                    .padding(vertical = 12.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDurationDialog = false }) {
                    Text("Cancel", color = color)
                }
            }
        )
    }
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}
