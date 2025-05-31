package com.example.growpath.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.growpath.model.Milestone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadmapScreen(
    roadmapId: String,
    onBackClick: () -> Unit,
    onMilestoneClick: (String) -> Unit,
    viewModel: RoadmapViewModel = hiltViewModel()
) {
    LaunchedEffect(roadmapId) {
        viewModel.loadMilestones(roadmapId)
    }

    val state by viewModel.state.collectAsState()
    val snackbarHostState = SnackbarHostState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.roadmapTitle ?: "Learning Path") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5)) // Light gray background like Duolingo
        ) {
            // Loading indicator
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Error message
            state.error?.let {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error: $it",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadMilestones(roadmapId) }) {
                        Text("Retry")
                    }
                }
            }

            // Content when loaded
            AnimatedVisibility(
                visible = !state.isLoading && state.error == null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Roadmap progress indicator
                    RoadmapProgressHeader(
                        milestones = state.milestones,
                        modifier = Modifier.padding(16.dp)
                    )

                    // Duolingo-style path
                    DuolingoStylePath(
                        milestones = state.milestones,
                        onMilestoneClick = onMilestoneClick
                    )
                }
            }
        }
    }
}

@Composable
fun RoadmapProgressHeader(
    milestones: List<Milestone>,
    modifier: Modifier = Modifier
) {
    val completedCount = milestones.count { it.isCompleted }
    val totalCount = milestones.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    val progressAnimation by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000),
        label = "progress"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$completedCount/$totalCount",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                // Background track
                LinearProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(CircleShape),
                    color = Color.Gray.copy(alpha = 0.2f), // Mengubah background track menjadi abu-abu transparan
                    trackColor = Color.Gray.copy(alpha = 0.2f)
                )

                // Progress indicator
                LinearProgressIndicator(
                    progress = { progressAnimation },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.Gray.copy(alpha = 0.2f) // Mengubah track menjadi abu-abu transparan
                )

                Text(
                    text = "${(progress * 100).toInt()}%",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(top = 16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun DuolingoStylePath(
    milestones: List<Milestone>,
    onMilestoneClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Background decoration for visual interest
    Box(modifier = Modifier.fillMaxSize()) {
        // Main content
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy((-60).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(milestones) { index, milestone ->
                val isEvenIndex = index % 2 == 0
                val alignment = if (isEvenIndex) Alignment.CenterStart else Alignment.CenterEnd

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    // Draw connecting line to previous node - PUT FIRST to ensure it's at the back layer
                    if (index > 0) {
                        val previousCompleted = milestones[index-1].isCompleted
                        val currentMilestoneCompleted = milestone.isCompleted
                        val primaryColor = MaterialTheme.colorScheme.primary
                        val lightGrayColor = Color.LightGray

                        val lineColor = if (previousCompleted && currentMilestoneCompleted)
                            primaryColor else lightGrayColor

                        val previousIsEven = (index - 1) % 2 == 0
                        Canvas(modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(vertical = 4.dp)
                        ) {
                            val startX = if (previousIsEven) size.width * 0.2f else size.width * 0.8f
                            val endX = if (isEvenIndex) size.width * 0.2f else size.width * 0.8f
                            val startY = 0f // Top of this Canvas
                            val endY = size.height // Bottom of this Canvas

                            val lPath = Path().apply {
                                moveTo(startX, startY)
                                lineTo(endX, startY)   // Horizontal segment
                                lineTo(endX, endY)     // Vertical segment
                            }

                            // Add highlight effect to line (shadow/glow)
                            if (previousCompleted && currentMilestoneCompleted) {
                                // Draw background glow for completed path
                                drawPath(
                                    path = lPath,
                                    color = primaryColor.copy(alpha = 0.3f),
                                    style = Stroke(
                                        width = 6.dp.toPx(), // Wider for glow
                                        cap = StrokeCap.Round // Optional: for softer edges
                                    )
                                )
                            }

                            // Draw actual L-shaped path on top
                            drawPath(
                                path = lPath,
                                color = lineColor,
                                style = Stroke(
                                    width = 3.dp.toPx(),
                                    pathEffect = if (previousCompleted && currentMilestoneCompleted) null else PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                )
                            )
                        }
                    }

                    // Milestone node
                    LearningPathNode(
                        milestone = milestone,
                        onClick = { onMilestoneClick(milestone.id) },
                        modifier = Modifier
                            .align(alignment)
                            .padding(top = if (index > 0) 80.dp else 0.dp)
                    )
                }
            }

            // Add some padding at the end
            item { Spacer(modifier = Modifier.height(150.dp)) }
        }
    }
}

@Composable
fun LearningPathNode(
    milestone: Milestone,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nodeColor = if (milestone.isCompleted) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.White
    }

    val textColor = if (milestone.isCompleted) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val iconTint = if (milestone.isCompleted) {
        Color.White
    } else {
        MaterialTheme.colorScheme.primary
    }

    // Ukuran node yang lebih besar lagi
    val nodeSize = 160.dp
    val shadowElevation = if (milestone.isCompleted) 12.dp else 8.dp // Shadow lebih dramatis

    // Using key to ensure animation state is preserved correctly
    key(milestone.id) {
        // Add pulse animation for completed nodes - fixing Composable context
        val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = if (milestone.isCompleted) 1.05f else 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseAnimation"
        )

        val borderGlow by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = if (milestone.isCompleted) 0.8f else 0.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500),
                repeatMode = RepeatMode.Reverse
            ),
            label = "borderAnimation"
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.width(nodeSize + 48.dp) // Wider column for larger node
        ) {
            // Capture MaterialTheme colors outside of drawBehind
            val primaryColor = MaterialTheme.colorScheme.primary

            Box(
                modifier = Modifier
                    .size(nodeSize)
                    // Inner glow effect for completed nodes
                    .then(
                        if (milestone.isCompleted) {
                            Modifier.drawBehind {
                                drawCircle(
                                    color = primaryColor.copy(alpha = 0.2f),
                                    radius = size.width * 0.52f
                                )
                            }
                        } else {
                            Modifier
                        }
                    )
                    .shadow(shadowElevation, CircleShape)
                    .background(nodeColor, CircleShape)
                    .border(
                        width = 5.dp, // Border lebih tebal
                        color = if (milestone.isCompleted)
                               primaryColor.copy(alpha = borderGlow)
                               else primaryColor.copy(alpha = 0.7f),
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .clickable(onClick = onClick)
                    .padding(4.dp) // Padding to separate content from border
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                    }, // Apply pulse animation scale correctly
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp) // Padding dalam yang lebih besar
                ) {
                    // Icon yang lebih besar - only show for completed nodes or nodes with specific titles
                    if (milestone.isCompleted ||
                        milestone.title.contains("intro", ignoreCase = true) ||
                        milestone.title.contains("final", ignoreCase = true) ||
                        milestone.title.contains("test", ignoreCase = true) ||
                        milestone.title.contains("practice", ignoreCase = true)) {

                        Icon(
                            imageVector = when {
                                milestone.title.contains("intro", ignoreCase = true) -> Icons.Default.School
                                milestone.title.contains("final", ignoreCase = true) -> Icons.Default.EmojiEvents
                                milestone.title.contains("test", ignoreCase = true) -> Icons.Default.Quiz
                                milestone.title.contains("practice", ignoreCase = true) -> Icons.Default.Build
                                else -> Icons.Default.CheckCircle // Only for completed nodes
                            },
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(48.dp) // Ukuran ikon lebih besar
                        )

                        Spacer(modifier = Modifier.height(12.dp)) // Jarak yang lebih besar
                    }

                    Text(
                        text = milestone.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 16.sp
                    )

                    // Description removed as requested
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status indicator with pill shape and non-transparent text
            Surface(
                color = if (milestone.isCompleted)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else
                    Color.White,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .padding(top = 4.dp)
                    .then(
                        if (milestone.isCompleted) {
                            Modifier.border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = borderGlow),
                                shape = RoundedCornerShape(50)
                            )
                        } else {
                            Modifier
                        }
                    )
            ) {
                Text(
                    text = if (milestone.isCompleted) "COMPLETED" else "PENDING",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (milestone.isCompleted)
                        MaterialTheme.colorScheme.primary // Non-transparent text
                    else
                        Color.Gray, // Non-transparent text
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 12.sp
                )
            }
        }
    }
}
