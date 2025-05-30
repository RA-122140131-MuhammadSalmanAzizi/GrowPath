package com.example.growpath.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.growpath.model.Roadmap

@Composable
fun RoadmapCardEnhanced(
    roadmap: Roadmap,
    onClick: () -> Unit,
    onFavoriteClick: (Roadmap) -> Unit = {},
    isFavorite: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White  // Setting card background to white
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Card Header with title and category
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon based on difficulty
                val (icon, iconColor) = when {
                    roadmap.title.contains("Advanced", ignoreCase = true) ->
                        Icons.Default.Star to Color(0xFFF44336) // Red for advanced
                    roadmap.title.contains("Intermediate", ignoreCase = true) ->
                        Icons.Default.ArrowUpward to Color(0xFFFF9800) // Orange for intermediate
                    else ->
                        Icons.Default.School to Color(0xFF4CAF50) // Green for beginner
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = roadmap.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Determine category from title or use a default
                    val category = when {
                        roadmap.title.contains("Android", ignoreCase = true) -> "Android Development"
                        roadmap.title.contains("Web", ignoreCase = true) -> "Web Development"
                        roadmap.title.contains("UI", ignoreCase = true) ||
                        roadmap.title.contains("UX", ignoreCase = true) -> "UI/UX Design"
                        roadmap.title.contains("Backend", ignoreCase = true) -> "Backend Development"
                        roadmap.title.contains("AI", ignoreCase = true) -> "Artificial Intelligence"
                        roadmap.title.contains("ML", ignoreCase = true) -> "Machine Learning"
                        roadmap.title.contains("Kotlin", ignoreCase = true) -> "Kotlin Programming"
                        roadmap.title.contains("Flutter", ignoreCase = true) -> "Flutter Development"
                        roadmap.title.contains("iOS", ignoreCase = true) -> "iOS Development"
                        roadmap.title.contains("React", ignoreCase = true) -> "React Programming"
                        roadmap.title.contains("Node", ignoreCase = true) -> "Node.js Development"
                        roadmap.title.contains("Python", ignoreCase = true) -> "Python Programming"
                        roadmap.title.contains("Java", ignoreCase = true) -> "Java Programming"
                        else -> "Programming"
                    }

                    Text(
                        text = category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status indicator
                val (statusIcon, statusText, statusColor) = when {
                    roadmap.progress >= 1.0f -> Triple(
                        Icons.Default.CheckCircle,
                        "Completed",
                        Color(0xFF4CAF50)  // Green
                    )
                    roadmap.progress > 0f -> Triple(
                        Icons.Default.Autorenew,
                        "In Progress",
                        Color(0xFF2196F3)  // Blue
                    )
                    else -> Triple(
                        Icons.Default.PlayCircleOutline,
                        "Not Started",
                        Color(0xFF9E9E9E)  // Grey
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = statusText,
                        tint = statusColor
                    )
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = roadmap.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar and stats
            Column {
                if (roadmap.progress > 0f) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${(roadmap.progress * 100).toInt()}% Complete",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Estimated time to complete
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Time Estimate",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))

                            // Simple time estimate based on progress
                            val remainingHours = ((1 - roadmap.progress) * 40).toInt()
                            val timeText = when {
                                remainingHours > 48 -> "${remainingHours / 24} days left"
                                remainingHours > 0 -> "$remainingHours hours left"
                                else -> "Complete"
                            }

                            Text(
                                text = timeText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress bar with love button to the right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = { roadmap.progress },
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Love icon button without background
                        IconButton(
                            onClick = { onFavoriteClick(roadmap) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) "Unfavorite" else "Favorite",
                                tint = if (isFavorite) Color.Red else Color.Gray
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Start Learning"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Start Learning")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Love icon button without background
                        IconButton(
                            onClick = { onFavoriteClick(roadmap) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) "Unfavorite" else "Favorite",
                                tint = if (isFavorite) Color.Red else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
