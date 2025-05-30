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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.growpath.model.Milestone
import kotlin.math.abs

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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                    color = Color.LightGray,
                    trackColor = Color.LightGray
                )

                // Progress indicator
                LinearProgressIndicator(
                    progress = { progressAnimation },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary
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
                .padding(horizontal = 16.dp, vertical = 16.dp), // Kurangi padding horizontal untuk node yang lebih besar
            verticalArrangement = Arrangement.spacedBy((-60).dp),  // Perbesar overlap untuk node yang lebih besar
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
                        val previousCompleted = index > 0 && milestones[index-1].isCompleted
                        val lineColor = if (previousCompleted && milestone.isCompleted)
                            MaterialTheme.colorScheme.primary else Color.LightGray

                        val previousIsEven = (index - 1) % 2 == 0
                        Canvas(modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp) // Tinggi canvas lebih besar untuk jalur lengkung yang lebih dramatis
                            .padding(vertical = 4.dp) // Make sure there's room for the path
                        ) {
                            val startX = if (previousIsEven) size.width * 0.2f else size.width * 0.8f
                            val endX = if (isEvenIndex) size.width * 0.2f else size.width * 0.8f
                            val startY = 0f
                            val endY = size.height

                            // Add highlight effect to line (shadow/glow)
                            if (previousCompleted && milestone.isCompleted) {
                                // Draw background glow for completed path
                                val glowPath = Path().apply {
                                    moveTo(startX, startY)
                                    cubicTo(
                                        startX, startY + size.height * 0.3f,
                                        endX, endY - size.height * 0.3f,
                                        endX, endY
                                    )
                                }

                                drawPath(
                                    path = glowPath,
                                    color = lineColor.copy(alpha = 0.3f),
                                    style = Stroke(
                                        width = 22f, // Wider for glow effect
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }

                            // Draw main path
                            val mainPath = Path().apply {
                                moveTo(startX, startY)
                                cubicTo(
                                    startX, startY + size.height * 0.3f,
                                    endX, endY - size.height * 0.3f,
                                    endX, endY
                                )
                            }

                            // Draw the much thicker path
                            drawPath(
                                path = mainPath,
                                color = lineColor,
                                style = Stroke(
                                    width = 16f, // Double the thickness from previous 8f
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )

                            // For completed paths, add decorative dots at intervals
                            if (previousCompleted && milestone.isCompleted) {
                                val pathMeasure = android.graphics.PathMeasure(mainPath.asAndroidPath(), false)
                                val pathLength = pathMeasure.length
                                val pos = FloatArray(2)
                                val tan = FloatArray(2)

                                // Add dots along the path
                                for (i in 0 until (pathLength / 50).toInt()) {
                                    pathMeasure.getPosTan(i * 50f, pos, tan)
                                    drawCircle(
                                        color = Color.White,
                                        radius = 4f,
                                        center = Offset(pos[0], pos[1])
                                    )
                                }
                            }
                        }
                    }

                    // Milestone node
                    LearningPathNode(
                        milestone = milestone,
                        onClick = { onMilestoneClick(milestone.id) },
                        modifier = Modifier
                            .align(alignment)
                            .padding(top = if (index > 0) 80.dp else 0.dp) // Padding atas yang lebih besar
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
            Box(
                modifier = Modifier
                    .size(nodeSize)
                    // Inner glow effect for completed nodes
                    .then(
                        if (milestone.isCompleted) {
                            Modifier.drawBehind {
                                drawCircle(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
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
                               MaterialTheme.colorScheme.primary.copy(alpha = borderGlow)
                               else MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
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
                    // Icon yang lebih besar
                    Icon(
                        imageVector = when {
                            milestone.title.contains("intro", ignoreCase = true) -> Icons.Default.School
                            milestone.title.contains("final", ignoreCase = true) -> Icons.Default.EmojiEvents
                            milestone.title.contains("test", ignoreCase = true) -> Icons.Default.Quiz
                            milestone.title.contains("practice", ignoreCase = true) -> Icons.Default.Build
                            else -> Icons.Default.CheckCircle
                        },
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(48.dp) // Ukuran ikon lebih besar (dari 36.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp)) // Jarak yang lebih besar

                    Text(
                        text = milestone.title,
                        style = MaterialTheme.typography.titleMedium, // Larger text style
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        textAlign = TextAlign.Center,
                        maxLines = 3, // Allow more lines for better readability with larger text
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 16.sp // Font lebih besar (dari 14.sp)
                    )

                    if (milestone.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = milestone.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (milestone.isCompleted) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status indicator with pill shape and animated border for completed items
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
                        MaterialTheme.colorScheme.primary
                    else
                        Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), // Padding lebih besar
                    fontSize = 12.sp // Font status lebih besar
                )
            }
        }
    }
}
