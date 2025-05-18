package com.example.growpath.presentation.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import com.example.growpath.data.model.Milestone

object GraphUtils {
    /**
     * Calculates an optimal layout for milestones in a graph based on their dependencies
     * @param milestones The list of milestones to arrange
     * @param width The available width
     * @param height The available height
     * @return A list of milestones with updated x and y positions
     */
    fun calculateGraphLayout(
        milestones: List<Milestone>,
        width: Float,
        height: Float
    ): List<Milestone> {
        // This is a simplified implementation
        // A real implementation would use a more sophisticated algorithm like
        // force-directed graph layout or hierarchical layout

        // Group milestones by their position (level)
        val milestonesByLevel = milestones.groupBy { it.position }
        val levels = milestonesByLevel.keys.sorted()

        // Calculate horizontal and vertical spacing
        val levelCount = levels.size
        val horizontalSpacing = width / (levelCount + 1)

        return milestones.map { milestone ->
            val level = milestone.position
            val levelIndex = levels.indexOf(level)

            // Calculate x position based on level
            val xPos = (levelIndex + 1) * horizontalSpacing / width

            // Calculate y position based on number of milestones at this level
            val milestonesAtThisLevel = milestonesByLevel[level] ?: listOf()
            val milestoneIndex = milestonesAtThisLevel.indexOf(milestone)
            val verticalSpacing = height / (milestonesAtThisLevel.size + 1)
            val yPos = (milestoneIndex + 1) * verticalSpacing / height

            milestone.copy(xPosition = xPos, yPosition = yPos)
        }
    }

    /**
     * Creates a curved path between two points for the graph
     * @param start The starting point
     * @param end The ending point
     * @param curvature How much the path should curve (0-1)
     * @return A path connecting the points with a curve
     */
    fun createCurvedPath(start: Offset, end: Offset, curvature: Float = 0.5f): Path {
        return Path().apply {
            moveTo(start.x, start.y)

            // Create a curved path with control points
            val midX = (start.x + end.x) / 2
            val midY = (start.y + end.y) / 2

            // Control point offset creates the curve
            val controlPointOffset = (end.x - start.x) * curvature

            // Use quadratic Bezier curve
            quadraticBezierTo(
                midX + controlPointOffset,
                midY - controlPointOffset,
                end.x,
                end.y
            )
        }
    }

    /**
     * Determines if a milestone is accessible based on its dependencies
     * @param milestone The milestone to check
     * @param completedMilestoneIds IDs of milestones that are completed
     * @return True if the milestone can be started
     */
    fun isMilestoneAccessible(
        milestone: Milestone,
        completedMilestoneIds: Set<String>
    ): Boolean {
        // If it's already completed, it's accessible
        if (milestone.isCompleted) {
            return true
        }

        // If it has no dependencies, it's accessible
        if (milestone.dependencies.isEmpty()) {
            return true
        }

        // Otherwise, all dependencies must be completed
        return milestone.dependencies.all { dependencyId ->
            completedMilestoneIds.contains(dependencyId)
        }
    }
}
