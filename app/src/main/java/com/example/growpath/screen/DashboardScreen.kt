package com.example.growpath.screen

import androidx.compose.animation.core.*
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.growpath.GrowPathApp
import com.example.growpath.component.HomeStatisticsWidget
import com.example.growpath.component.HomeWidgetsGrid
import com.example.growpath.component.SpinningRefreshIndicator
import com.example.growpath.component.UpcomingMilestoneWidget
import com.example.growpath.component.getHomeWidgets
import com.example.growpath.model.Roadmap
import com.example.growpath.navigation.NavGraph
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onRoadmapClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
    notificationsViewModel: NotificationsViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val state by viewModel.state.collectAsState()
    val notificationsState by notificationsViewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = state.isLoading)

    LaunchedEffect(key1 = state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    // Observe dashboard events for notifications
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is DashboardEvent.RoadmapCompleted -> {
                    notificationsViewModel.addNotification(
                        title = event.title,
                        message = event.message
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Menggunakan Text dengan Brush untuk memberikan efek gradasi pada teks
                        Text(
                            text = "GrowPath",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF38B54C), // Cyan/teal
                                        Color(0xFF71C6C3)  // Light cyan
                                    )
                                ),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                actions = {
                    // Notification Bell Icon with badge if there are unread notifications
                    BadgedBox(
                        badge = {
                            if (notificationsState.unreadCount > 0) {
                                Badge(
                                    modifier = Modifier.offset(x = (-6).dp, y = 6.dp) // Geser posisi badge agar tidak terpotong
                                ) {
                                    Text(
                                        text = if (notificationsState.unreadCount > 9) "9+" else notificationsState.unreadCount.toString()
                                    )
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = {
                                navController?.navigate(NavGraph.NOTIFICATIONS)
                                // Tidak menandai notifikasi sebagai telah dibaca saat mengklik ikon
                                // Dibaca hanya setelah user melihat atau klik tombol "Mark All as Read"
                            },
                            modifier = Modifier.padding(end = 8.dp) // Tambahkan padding agar tidak terlalu pinggir
                        ) {
                            Icon(
                                Icons.Outlined.Notifications,
                                contentDescription = "Notifications"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = Color(0xFF2196F3) // Change to your preferred color
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
                containerColor = Color(0xFF58A9A8), // Changed to blue color
                contentColor = Color.White
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent // Membuat container Scaffold menjadi transparan
    ) { paddingValuesFromDashboardScaffold -> // These are for TopAppBar etc. of DashboardScreen
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                // Play a sound effect when refresh is released
                navController?.context?.let { context ->
                    try {
                        val audioManager = context.getSystemService(android.content.Context.AUDIO_SERVICE) as android.media.AudioManager
                        audioManager.playSoundEffect(android.media.AudioManager.FX_KEY_CLICK)
                    } catch (e: Exception) {
                        // Silently handle any errors with sound playback
                    }
                }
                // Reload page data
                viewModel.onRefresh()
            },
            modifier = Modifier
                .padding(paddingValuesFromDashboardScaffold) // Apply Scaffold's padding here
                .fillMaxSize() // SwipeRefresh fills the padded area
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize() // LazyColumn fills SwipeRefresh
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White,
                                // Pastikan warna ini sesuai dengan yang Anda inginkan untuk gradient
                                Color(0xFFBCEAE7)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 8.dp, top = 8.dp) // Adjusted bottom padding for content inside LazyColumn
            ) {
                item {
                    UserProgressCard(
                        userName = state.userName,
                        userLevel = state.userLevel,
                        userExperience = state.userExperience,
                        onProfileClick = onProfileClick
                    )
                }

                // Statistics Section
                item {
                    HomeStatisticsWidget(
                        completedToday = state.completedToday,
                        streak = state.currentStreak,
                        totalXp = state.userExperience,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // Home Widget Section (Pomodoro)
                item {
                    HomeWidgetsGrid(
                        widgets = getHomeWidgets(navController),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // Continue Learning Widget - menampilkan roadmap terakhir yang dibuka
                item {
                    // Tampilkan roadmap terakhir yang dibuka jika ada, jika tidak tampilkan roadmap yang sedang in progress
                    if (state.lastOpenedRoadmap != null) {
                        // Menggunakan roadmap terakhir dibuka
                        val lastRoadmap = state.lastOpenedRoadmap
                        UpcomingMilestoneWidget(
                            title = lastRoadmap?.title ?: "Unknown Title",
                            description = lastRoadmap?.description ?: "No description available",
                            progress = lastRoadmap?.progress ?: 0f,
                            onClick = {
                                lastRoadmap?.id?.let { id ->
                                    viewModel.onRoadmapClick(id)
                                    onRoadmapClick(id)
                                }
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    } else if (state.inProgressRoadmaps.isNotEmpty()) {
                        // Fallback ke roadmap pertama dalam daftar "In Progress"
                        val firstInProgress = state.inProgressRoadmaps.first()
                        UpcomingMilestoneWidget(
                            title = firstInProgress.title,
                            description = firstInProgress.description,
                            progress = firstInProgress.progress,
                            onClick = {
                                viewModel.onRoadmapClick(firstInProgress.id)
                                onRoadmapClick(firstInProgress.id)
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                // In Progress Roadmaps
                item {
                    SectionHeader(title = "In Progress",
                                  subtitle = "Continue your learning journey",
                                  icon = Icons.Default.School)

                    if (state.inProgressRoadmaps.isEmpty()) {
                        EmptyStateMessage(
                            message = "No roadmaps in progress. Start a new journey!",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    } else {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.inProgressRoadmaps) { roadmap ->
                                RoadmapCard(
                                    roadmap = roadmap,
                                    onClick = {
                                        viewModel.onRoadmapClick(roadmap.id)
                                        onRoadmapClick(roadmap.id)
                                    }
                                )
                            }
                        }
                    }
                }

                // Popular Roadmaps updated to Favorite Roadmaps
                item {
                    SectionHeader(title = "Favorite",
                                  subtitle = "Your saved learning paths",
                                  icon = Icons.Default.Favorite)

                    if (state.favoriteRoadmaps.isEmpty()) {
                        EmptyStateMessage(
                            message = "No favorite roadmaps yet. Add some from Explore!",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    } else {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.favoriteRoadmaps) { roadmap ->
                                RoadmapCard(
                                    roadmap = roadmap,
                                    onClick = {
                                        viewModel.onRoadmapClick(roadmap.id)
                                        onRoadmapClick(roadmap.id)
                                    }
                                )
                            }
                        }
                    }
                }

                // Completed Roadmaps
                item {
                    SectionHeader(title = "Completed",
                                  subtitle = "Your achievements",
                                  icon = Icons.Default.Check)

                    if (state.completedRoadmaps.isEmpty()) {
                        EmptyStateMessage(
                            message = "No completed roadmaps yet. Keep learning!",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    } else {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.completedRoadmaps) { roadmap ->
                                RoadmapCard(
                                    roadmap = roadmap,
                                    isCompleted = true,
                                    onClick = {
                                        viewModel.onRoadmapClick(roadmap.id)
                                        onRoadmapClick(roadmap.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RoadmapCard(
    roadmap: Roadmap,
    isCompleted: Boolean = false,
    onClick: () -> Unit
) {
    // Changed the backgroundColor to white (Color.White) as requested
    val backgroundColor = Color.White

    Card(
        modifier = Modifier
            .width(180.dp)
            .height(160.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Title and description
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = roadmap.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = roadmap.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Progress indicator and completion status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Completed",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    LinearProgressIndicator(
                        progress = roadmap.progress,
                        modifier = Modifier.weight(1f),
                        trackColor = Color.Gray.copy(alpha = 0.2f) // Mengubah track menjadi abu-abu transparan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${(roadmap.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun UserProgressCard(
    userName: String,
    userLevel: Int,  // Ini adalah level dari data state, mungkin tidak sesuai dengan XP
    userExperience: Int,
    onProfileClick: () -> Unit
) {
    // Hitung level yang benar berdasarkan XP
    val xpPerLevel = 1000 // XP yang dibutuhkan per level
    val calculatedLevel = (userExperience / xpPerLevel) + 1 // Level dimulai dari 1
    val currentLevelXp = userExperience % xpPerLevel
    val xpToNextLevel = xpPerLevel - currentLevelXp
    val progress = currentLevelXp.toFloat() / xpPerLevel.toFloat()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF66D2CC),
                            Color(0xFF6CB8B7)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // User avatar
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "User Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Welcome back,",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    BadgeBox(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.Star,
                                contentDescription = "Level",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Level $calculatedLevel",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Level progress
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$userExperience XP",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                        Text(
                            text = "To next level: $xpToNextLevel XP",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Progress bar for level
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
fun BadgeBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()
    }
}

