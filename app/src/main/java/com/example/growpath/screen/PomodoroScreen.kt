package com.example.growpath.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(onBackClick: () -> Unit) {
    var sessionDuration by remember { mutableStateOf(25) } // in minutes
    var breakDuration by remember { mutableStateOf(5) } // in minutes
    var timeLeft by remember { mutableStateOf(sessionDuration * 60) }
    var isBreak by remember { mutableStateOf(false) }
    var isActive by remember { mutableStateOf(false) }
    var sessionDropdownExpanded by remember { mutableStateOf(false) }
    var breakDropdownExpanded by remember { mutableStateOf(false) }
    val minuteOptions = (1..60).toList()

    val workGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF2196F3), Color(0xFF64B5F6))
    )
    val breakGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF43EA7B), Color(0xFFB2FF59))
    )
    val backgroundBrush = if (isBreak) breakGradient else workGradient

    // Timer only active if isActive (study session) or always on break
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
                title = { Text("Pomodoro Timer") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isBreak) Color(0xFF43EA7B) else Color(0xFF2196F3),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundBrush)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Fantastis Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .shadow(16.dp, shape = MaterialTheme.shapes.large),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.85f)
                    ),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Dropdown for session and break duration
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box {
                                ExposedDropdownMenuBox(
                                    expanded = sessionDropdownExpanded,
                                    onExpandedChange = { sessionDropdownExpanded = !sessionDropdownExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = sessionDuration.toString(),
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Session (min)") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sessionDropdownExpanded) },
                                        modifier = Modifier.menuAnchor().width(140.dp)
                                    )
                                    ExposedDropdownMenu(
                                        expanded = sessionDropdownExpanded,
                                        onDismissRequest = { sessionDropdownExpanded = false }
                                    ) {
                                        minuteOptions.forEach { min ->
                                            DropdownMenuItem(
                                                text = { Text(min.toString(), textAlign = TextAlign.Center) },
                                                onClick = {
                                                    sessionDuration = min
                                                    if (!isBreak) timeLeft = sessionDuration * 60
                                                    sessionDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Box {
                                ExposedDropdownMenuBox(
                                    expanded = breakDropdownExpanded,
                                    onExpandedChange = { breakDropdownExpanded = !breakDropdownExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = breakDuration.toString(),
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Break (min)") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = breakDropdownExpanded) },
                                        modifier = Modifier.menuAnchor().width(140.dp)
                                    )
                                    ExposedDropdownMenu(
                                        expanded = breakDropdownExpanded,
                                        onDismissRequest = { breakDropdownExpanded = false }
                                    ) {
                                        minuteOptions.forEach { min ->
                                            DropdownMenuItem(
                                                text = { Text(min.toString(), textAlign = TextAlign.Center) },
                                                onClick = {
                                                    breakDuration = min
                                                    if (isBreak) timeLeft = breakDuration * 60
                                                    breakDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = if (isBreak) "Break Time!" else "Study Session",
                            style = MaterialTheme.typography.headlineSmall,
                            color = if (isBreak) Color(0xFF43A047) else Color(0xFF1976D2)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        // Timer Circle
                        Box(
                            modifier = Modifier
                                .size(220.dp)
                                .shadow(12.dp, shape = MaterialTheme.shapes.large)
                                .background(
                                    brush = if (isBreak)
                                        Brush.radialGradient(listOf(Color(0xFFB2FF59), Color(0xFF43EA7B)))
                                    else
                                        Brush.radialGradient(listOf(Color(0xFF64B5F6), Color(0xFF2196F3))),
                                    shape = MaterialTheme.shapes.large
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = formatTime(timeLeft),
                                style = MaterialTheme.typography.displayLarge,
                                color = if (isBreak) Color(0xFF388E3C) else Color(0xFF1565C0)
                            )
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        if (!isBreak) {
                            Button(
                                onClick = { isActive = true },
                                enabled = !isActive && timeLeft == sessionDuration * 60,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3), contentColor = Color.White)
                            ) {
                                Text("Start")
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                isBreak = false
                                isActive = false
                                timeLeft = sessionDuration * 60
                            },
                            enabled = timeLeft < sessionDuration * 60 || isBreak,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD), contentColor = Color.Black)
                        ) {
                            Text("Reset")
                        }
                    }
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

