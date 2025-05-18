package com.example.growpath.presentation.roadmap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.growpath.data.model.Milestone

@Composable
fun RoadmapGraph(
    milestones: List<Milestone>,
    onMilestoneClick: (String) -> Unit,
    onMilestoneComplete: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.5f, 3f)

                    val newOffset = offset + pan
                    offset = newOffset
                }
            }
    ) {
        // Draw connections between milestones
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val width = size.width
            val height = size.height

            milestones.forEach { milestone ->
                milestone.dependencies.forEach { dependencyId ->
                    val dependency = milestones.find { it.id == dependencyId } ?: return@forEach

                    val startX = width * dependency.xPosition + offset.x
                    val startY = height * dependency.yPosition + offset.y
                    val endX = width * milestone.xPosition + offset.x
                    val endY = height * milestone.yPosition + offset.y

                    val path = Path().apply {
                        moveTo(startX, startY)

                        // Create a curved path between the two points
                        val controlX = (startX + endX) / 2
                        val controlY = (startY + endY) / 2 - 50 * scale

                        quadraticBezierTo(controlX, controlY, endX, endY)
                    }

                    // Draw the path with different color based on completion
                    val pathColor = if (dependency.isCompleted && milestone.isCompleted) {
                        Color.Green.copy(alpha = 0.7f)
                    } else {
                        Color.Gray.copy(alpha = 0.5f)
                    }

                    drawPath(
                        path = path,
                        color = pathColor,
                        style = Stroke(width = 2.dp.toPx() * scale, cap = StrokeCap.Round)
                    )
                }
            }
        }

        // Draw milestone nodes
        milestones.forEach { milestone ->
            MilestoneNode(
                milestone = milestone,
                onClick = { onMilestoneClick(milestone.id) },
                onCompleteToggle = { isCompleted ->
                    onMilestoneComplete(milestone.id, isCompleted)
                },
                modifier = Modifier
                    .offset(
                        x = (milestone.xPosition * 1000).dp * scale + offset.x.dp / 3,
                        y = (milestone.yPosition * 1000).dp * scale + offset.y.dp / 3
                    )
                    .size(60.dp * scale)
            )
        }
    }
}

@Composable
fun MilestoneNode(
    milestone: Milestone,
    onClick: () -> Unit,
    onCompleteToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Node background
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (milestone.isCompleted) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (milestone.isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = milestone.position.toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Tooltip with milestone title on hover
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = milestone.title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(top = 56.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    shape = MaterialTheme.shapes.small
                )
                .padding(4.dp)
        )
    }
}
