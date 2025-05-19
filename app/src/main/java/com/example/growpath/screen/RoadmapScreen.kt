package com.example.growpath.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.growpath.model.Milestone
import com.example.growpath.utils.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadmapScreen(
    roadmapId: String,
    onBackClick: () -> Unit,
    onMilestoneClick: (String) -> Unit,
    viewModel: RoadmapViewModel = viewModel(factory = ViewModelFactory())
) {
    LaunchedEffect(roadmapId) {
        viewModel.loadMilestones(roadmapId)
    }

    val state by viewModel.state.collectAsState()
    val snackbarHostState = androidx.compose.material3.SnackbarHostState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.roadmapTitle ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Loading indicator
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
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
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    // Milestone list
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(state.milestones) { index, milestone ->
                            MilestoneListItem(
                                milestone = milestone,
                                isFirst = index == 0,
                                isLast = index == state.milestones.size - 1,
                                onClick = { onMilestoneClick(milestone.id) },
                                onCheckToggle = { isChecked ->
                                    viewModel.onMilestoneComplete(milestone.id, isChecked)
                                }
                            )
                        }
                    }
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

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Roadmap Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$completedCount of $totalCount milestones completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MilestoneListItem(
    milestone: Milestone,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit,
    onCheckToggle: (Boolean) -> Unit
) {
    val lineColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)

    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline line and circle
        Box(
            modifier = Modifier
                .width(56.dp)
                .padding(end = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            // The vertical line
            if (!isFirst || !isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(if (isFirst || isLast) 60.dp else 120.dp)
                        .background(color = lineColor)
                        .align(if (isFirst) Alignment.BottomCenter else if (isLast) Alignment.TopCenter else Alignment.Center)
                )
            }

            // The circle indicator
            Checkbox(
                checked = milestone.isCompleted,
                onCheckedChange = onCheckToggle,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(4.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            )
        }

        // Card with milestone details
        Card(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (milestone.isCompleted)
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                else
                    MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = milestone.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (milestone.isCompleted)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = milestone.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Status chip
                AssistChip(
                    onClick = { /* no-op */ },
                    label = { Text(if (milestone.isCompleted) "Completed" else "Pending") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (milestone.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (milestone.isCompleted)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        labelColor = if (milestone.isCompleted)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        leadingIconContentColor = if (milestone.isCompleted)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
