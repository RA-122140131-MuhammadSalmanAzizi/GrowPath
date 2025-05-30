package com.example.growpath.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(
    notificationId: String,
    onBackClick: () -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val notification = state.notifications.find { it.id == notificationId }

    // Mark notification as read when opened
    LaunchedEffect(notificationId) {
        viewModel.markAsRead(notificationId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(notification?.title ?: "Notification Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (notification == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = "Notification not found",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Display notification details in a formatted way
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Format and display the detailed content
                val lines = notification.detailedContent.trim().lines()

                lines.forEach { line ->
                    when {
                        // Header 1 - starts with #
                        line.startsWith("# ") -> {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = line.substringAfter("# "),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        // Header 2 - starts with ##
                        line.startsWith("## ") -> {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = line.substringAfter("## "),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        // Bullet point - starts with -
                        line.startsWith("- ") -> {
                            Row(modifier = Modifier.padding(start = 8.dp)) {
                                Text(
                                    text = "•",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 8.dp, top = 3.dp)
                                )
                                Text(
                                    text = line.substringAfter("- "),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        // Bold text - surrounded by **
                        line.contains("**") -> {
                            val parts = line.split("**")
                            Row {
                                for (i in parts.indices) {
                                    if (i % 2 == 1) {
                                        // Bold text
                                        Text(
                                            text = parts[i],
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    } else if (parts[i].isNotEmpty()) {
                                        // Regular text
                                        Text(
                                            text = parts[i],
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                        // Empty line - add spacing
                        line.isEmpty() -> {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        // Regular text
                        else -> {
                            Text(
                                text = line,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
