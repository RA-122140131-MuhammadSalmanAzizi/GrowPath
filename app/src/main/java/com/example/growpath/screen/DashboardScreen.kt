package com.example.growpath.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.growpath.GrowPathApp
import com.example.growpath.component.HomeStatisticsWidget
import com.example.growpath.component.HomeWidgetsGrid
import com.example.growpath.component.UpcomingMilestoneWidget
import com.example.growpath.component.getHomeWidgets
import com.example.growpath.model.Roadmap
import com.example.growpath.navigation.NavGraph
import com.example.growpath.utils.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onRoadmapClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    viewModel: DashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = ViewModelFactory()
    ),
    navController: NavController? = null
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "GrowPath",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onRefresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    // Implement actual roadmap creation functionality
                    viewModel.onCreateRoadmapClick()
                    // Navigate to appropriate screen or show dialog
                    navController?.navigate(NavGraph.EXPLORE)
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Roadmap") },
                containerColor = Color(0xFF2196F3), // Changed to blue color
                contentColor = Color.White
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 88.dp) // Extra space for FAB
        ) {
            item {
                UserProgressCard(
                    userName = state.userName,
                    userLevel = state.userLevel,
                    userExperience = state.userExperience,
                    onProfileClick = onProfileClick
                )
            }

            // Home Widgets Section (menggantikan Quick Actions)
            item {
                HomeWidgetsGrid(
                    widgets = getHomeWidgets(navController),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Statistics Section
            item {
                HomeStatisticsWidget(
                    completedToday = 2,
                    streak = 5,
                    totalXp = state.userExperience,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Continue Learning Widget
            if (state.inProgressRoadmaps.isNotEmpty()) {
                item {
                    val firstInProgress = state.inProgressRoadmaps.first()
                    UpcomingMilestoneWidget(
                        title = firstInProgress.title,
                        description = firstInProgress.description,
                        progress = firstInProgress.progress,
                        onClick = { onRoadmapClick(firstInProgress.id) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // In Progress Roadmaps
            if (state.inProgressRoadmaps.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "In Progress",
                        icon = Icons.Default.Timeline,
                        count = state.inProgressRoadmaps.size,
                        navController = navController,
                        categoryType = "in_progress"
                    )
                }

                items(state.inProgressRoadmaps) { roadmap ->
                    RoadmapCardEnhanced(
                        roadmap = roadmap,
                        onClick = { onRoadmapClick(roadmap.id) }
                    )
                }
            }

            // Not Started Roadmaps
            if (state.notStartedRoadmaps.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Not Started",
                        icon = Icons.Default.PlayCircleOutline,
                        count = state.notStartedRoadmaps.size,
                        navController = navController,
                        categoryType = "not_started"
                    )
                }

                items(state.notStartedRoadmaps) { roadmap ->
                    RoadmapCardEnhanced(
                        roadmap = roadmap,
                        onClick = { onRoadmapClick(roadmap.id) }
                    )
                }
            }

            // Completed Roadmaps
            if (state.completedRoadmaps.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Completed",
                        icon = Icons.Default.CheckCircle,
                        count = state.completedRoadmaps.size,
                        navController = navController,
                        categoryType = "completed"
                    )
                }

                items(state.completedRoadmaps) { roadmap ->
                    RoadmapCardEnhanced(
                        roadmap = roadmap,
                        onClick = { onRoadmapClick(roadmap.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun UserProgressCard(
    userName: String,
    userLevel: Int,
    userExperience: Int,
    onProfileClick: () -> Unit
) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(brush = gradientBrush)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // User Avatar
                    Surface(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .size(40.dp)
                                .padding(12.dp)
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column {
                        Text(
                            "Welcome back,",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                        Text(
                            userName.ifBlank { "User" },
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    // Level badge
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "$userLevel",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // XP Progress section
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Level Progress",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                        Text(
                            "${userExperience % 100}/100 XP",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { (userExperience % 100) / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.onPrimary,
                        trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Total: ${userExperience} XP",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Next level: ${(userLevel + 1)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionsSection(
    navController: NavController? = null
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                QuickActionItem(
                    icon = Icons.Default.Add,
                    title = "New Roadmap",
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { /* TODO: Add new roadmap functionality */ }
                )
            }
            item {
                QuickActionItem(
                    icon = Icons.Default.Search,
                    title = "Explore",
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = { navController?.navigate(NavGraph.EXPLORE) }
                )
            }
            item {
                QuickActionItem(
                    icon = Icons.Default.CheckCircle,
                    title = "Milestones",
                    color = MaterialTheme.colorScheme.tertiary,
                    // Just navigate to the first roadmap for now as an example
                    onClick = { navController?.navigate(NavGraph.roadmapWithId("1")) }
                )
            }
            item {
                QuickActionItem(
                    icon = Icons.Default.Star,
                    title = "Achievements",
                    color = Color(0xFFFFA000),
                    onClick = { navController?.navigate(NavGraph.ACHIEVEMENTS) }
                )
            }
        }
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        modifier = Modifier.width(100.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 8.dp)
                .clickable(onClick = onClick)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = color
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: ImageVector,
    count: Int,
    navController: NavController? = null,
    categoryType: String = ""
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.width(8.dp))
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    "$count",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(Modifier.weight(1f))
        TextButton(
            onClick = {
                // Navigate to the category view based on the type
                when (categoryType) {
                    "in_progress" -> navController?.navigate(NavGraph.EXPLORE + "?filter=in_progress")
                    "not_started" -> navController?.navigate(NavGraph.EXPLORE + "?filter=not_started")
                    "completed" -> navController?.navigate(NavGraph.EXPLORE + "?filter=completed")
                    else -> navController?.navigate(NavGraph.EXPLORE)
                }
            }
        ) {
            Text("View All")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadmapCardEnhanced(
    roadmap: Roadmap,
    onClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .animateContentSize(animationSpec = tween(200))
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Roadmap Icon
                val iconBgColor = when {
                    roadmap.progress >= 1f -> MaterialTheme.colorScheme.tertiary
                    roadmap.progress > 0f -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.primary
                }

                val icon = when {
                    roadmap.progress >= 1f -> Icons.Default.CheckCircle
                    roadmap.progress > 0f -> Icons.Default.Timeline
                    else -> Icons.Default.PlayCircleOutline
                }

                Surface(
                    shape = CircleShape,
                    color = iconBgColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = iconBgColor,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(24.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = roadmap.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = roadmap.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = if (expanded) 10 else 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Show less" else "Show more"
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Progress section
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${(roadmap.progress * 100).toInt()}% Complete",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    val chipText = when {
                        roadmap.progress >= 1f -> "Completed"
                        roadmap.progress > 0f -> "In Progress"
                        else -> "Not Started"
                    }

                    val chipColor = when {
                        roadmap.progress >= 1f -> MaterialTheme.colorScheme.tertiary
                        roadmap.progress > 0f -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.primary
                    }

                    AssistChip(
                        onClick = { /* no-op */ },
                        label = { Text(chipText) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = chipColor.copy(alpha = 0.12f),
                            labelColor = chipColor
                        )
                    )
                }

                Spacer(Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { roadmap.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = when {
                        roadmap.progress >= 1f -> MaterialTheme.colorScheme.tertiary
                        roadmap.progress > 0f -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.primary
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}
