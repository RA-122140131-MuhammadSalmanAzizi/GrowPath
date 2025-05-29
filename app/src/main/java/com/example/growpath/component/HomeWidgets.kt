package com.example.growpath.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.growpath.navigation.NavGraph

/**
 * Data class representing a home widget
 */
data class HomeWidget(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val secondaryColor: Color,
    val routeTo: String? = null,
    val action: (() -> Unit)? = null
)

/**
 * Get list of home widgets
 */
fun getHomeWidgets(navController: NavController? = null): List<HomeWidget> {
    return listOf(
        HomeWidget(
            id = "today_progress",
            title = "Today's Progress",
            description = "Continue your learning journey",
            icon = Icons.Default.PlayArrow,
            primaryColor = Color(0xFF4CAF50),
            secondaryColor = Color(0xFF2E7D32),
            action = {
                navController?.navigate(NavGraph.roadmapWithId("1")) {
                    // Allow navigation with back button working properly
                    launchSingleTop = true
                    restoreState = true
                }
            }
        ),
        HomeWidget(
            id = "discover",
            title = "Discover",
            description = "Find new learning paths",
            icon = Icons.Default.Explore,
            primaryColor = Color(0xFF2196F3),
            secondaryColor = Color(0xFF0D47A1),
            action = {
                navController?.navigate(NavGraph.EXPLORE) {
                    // Use the same navigation pattern as the bottom bar
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        ),
        HomeWidget(
            id = "achievements",
            title = "Achievements",
            description = "View your earned badges",
            icon = Icons.Default.EmojiEvents,
            primaryColor = Color(0xFFFF9800),
            secondaryColor = Color(0xFFE65100),
            action = {
                navController?.navigate(NavGraph.ACHIEVEMENTS) {
                    // Use the same navigation pattern as the bottom bar
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        ),
        HomeWidget(
            id = "pomodoro",
            title = "Pomodoro Timer",
            description = "Focus on your tasks",
            icon = Icons.Default.Timer,
            primaryColor = Color(0xFFE91E63),
            secondaryColor = Color(0xFFC2185B),
            action = {
                navController?.navigate(NavGraph.POMODORO_TIMER) {
                    // Allow navigation with back button working properly
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    )
}

@Composable
fun HomeWidgetsGrid(
    widgets: List<HomeWidget>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Widgets",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(180.dp), // Increased from 140dp to 180dp to show all items without scrolling
            content = {
                items(widgets) { widget ->
                    HomeWidgetItem(widget = widget)
                }
            }
        )
    }
}

@Composable
fun HomeWidgetItem(
    widget: HomeWidget,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            widget.primaryColor,
            widget.secondaryColor
        )
    )
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2.2f) // Increased aspect ratio to make items shorter
            .clickable { widget.action?.invoke() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBrush)
                .padding(10.dp) // Further reduced padding
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                Icon(
                    imageVector = widget.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp) // Smaller icon
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Text content
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = widget.title,
                        style = MaterialTheme.typography.bodyMedium, // Smaller text style
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = widget.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun HomeStatisticsWidget(
    completedToday: Int = 0,
    streak: Int = 0,
    totalXp: Int = 0,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp) // Reduced vertical padding from 12dp to 8dp
        ) {
            // Header with reduced spacing
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Your Statistics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.size(24.dp) // Reduced from 28dp to 24dp
                ) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(16.dp) // Reduced from 18dp to 16dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp)) // Reduced from 16dp to 12dp

            // Stat circles in a row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Today stat
                StatCircle(
                    value = completedToday.toString(),
                    label = "Today",
                    strokeColor = Color(0xFF36B54A) // Green color from SVG
                )

                // Streak stat with badge icon
                StatCircle(
                    value = streak.toString(),
                    label = "Streak",
                    strokeColor = Color(0xFF247BEA), // Blue color from SVG
                    showBadge = true
                )

                // XP stat
                StatCircle(
                    value = totalXp.toString(),
                    label = "XP",
                    strokeColor = Color(0xFF8759D2) // Purple color from SVG
                )
            }
        }
    }
}

@Composable
private fun StatCircle(
    value: String,
    label: String,
    strokeColor: Color,
    showBadge: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        // Circle with stroke - further reduced size from 60dp to 54dp
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(54.dp)
        ) {
            // Outer stroke circle
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(
                        width = 5.dp, // Reduced from 6dp to 5dp
                        color = strokeColor,
                        shape = CircleShape
                    )
            )
            
            // Value in the center - bold text
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold, // Changed from Bold to ExtraBold
                color = Color.DarkGray
            )
            
            // Show badge for streak if required
            if (showBadge) {
                Box(
                    modifier = Modifier
                        .size(18.dp) // Reduced from 20dp to 18dp
                        .align(Alignment.BottomCenter)
                        .offset(y = 4.dp) // Reduced from 5dp to 4dp
                        .clip(CircleShape)
                        .background(strokeColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(10.dp) // Reduced from 12dp to 10dp
                            .padding(1.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp)) // Reduced from 6dp to 4dp

        // Label with bold text
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold, // Added bold to label
            color = Color.DarkGray
        )
    }
}

@Composable
fun UpcomingMilestoneWidget(
    title: String,
    description: String,
    progress: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Continue Learning",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Continue"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${(progress * 100).toInt()}% Complete",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
