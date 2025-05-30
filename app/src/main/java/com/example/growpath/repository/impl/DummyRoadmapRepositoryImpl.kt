package com.example.growpath.repository.impl

import com.example.growpath.data.NotificationRepository
import com.example.growpath.model.Milestone
import com.example.growpath.model.Roadmap
import com.example.growpath.repository.RoadmapRepository
import com.example.growpath.screen.Notification
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DummyRoadmapRepositoryImpl @Inject constructor(
    private val notificationRepository: NotificationRepository
) : RoadmapRepository {
    // Make roadmaps and milestones mutable
    private val roadmaps = mutableListOf(
        Roadmap("1", "Android Development Journey", "Master Android development from scratch to expert level", 0.35f),
        Roadmap("2", "Jetpack Compose Mastery", "Become a UI expert with Jetpack Compose", 0.75f),
        Roadmap("3", "Kotlin Multiplatform", "Build cross-platform apps with KMM", 0.1f),
        Roadmap("4", "Firebase Integration", "Learn to integrate Firebase services in your app", 1.0f),
        Roadmap("5", "Material Design 3", "Create beautiful and intuitive UI with Material 3", 0.0f)
    )

    private val milestones = mutableMapOf(
        "1" to mutableListOf(
            Milestone("101", "1", "Setup Development Environment", "Install Android Studio and configure your workspace", true),
            Milestone("102", "1", "Kotlin Fundamentals", "Learn basic Kotlin syntax and concepts", true),
            Milestone("103", "1", "Android Basic UI", "Create layouts and basic UI components", false),
            Milestone("104", "1", "Activity & Fragment Lifecycle", "Master the Android component lifecycle", false),
            Milestone("105", "1", "Publishing Your App", "Learn to deploy your app to Google Play Store", false)
        ),
        "2" to mutableListOf(
            Milestone("201", "2", "Composable Functions", "Creating and using composable functions", true),
            Milestone("202", "2", "State Management", "Managing state in Compose apps", true),
            Milestone("203", "2", "Layouts & UI Hierarchy", "Creating complex layouts with Compose", true),
            Milestone("204", "2", "Animation & Effects", "Adding animations and visual effects to your UI", false)
        )
    )

    // Store milestone notes - milestoneId to note content
    private val milestoneNotes = mutableMapOf<String, String>()

    // Track completed roadmaps to avoid duplicate notifications
    private val completedRoadmapIds = mutableSetOf<String>()

    // Create MutableStateFlow objects to maintain and emit updated data
    private val _roadmapsFlow = MutableStateFlow(roadmaps.toList())
    private val _milestonesFlow = MutableStateFlow(milestones.toMap())
    private val _milestoneNotesFlow = MutableStateFlow(milestoneNotes.toMap())

    // Implement the required methods from RoadmapRepository interface
    override fun getRoadmaps(): Flow<List<Roadmap>> {
        return _roadmapsFlow.asStateFlow()
    }

    override fun getRoadmapById(roadmapId: String): Flow<Roadmap?> {
        return _roadmapsFlow.map { roadmaps ->
            roadmaps.find { it.id == roadmapId }
        }
    }

    override fun getMilestonesForRoadmap(roadmapId: String): Flow<List<Milestone>> {
        return _milestonesFlow.map { milestoneMap ->
            milestoneMap[roadmapId] ?: emptyList()
        }
    }

    override fun getMilestoneById(milestoneId: String): Flow<Milestone?> {
        return _milestonesFlow.map { milestoneMap ->
            milestoneMap.values.flatten().find { it.id == milestoneId }
        }
    }

    override suspend fun updateMilestoneCompletion(milestoneId: String, isCompleted: Boolean) {
        val allMilestones = _milestonesFlow.value.toMutableMap()

        // Find and update the milestone
        allMilestones.forEach { (roadmapId, roadmapMilestones) ->
            val milestoneIndex = roadmapMilestones.indexOfFirst { it.id == milestoneId }
            if (milestoneIndex != -1) {
                val milestone = roadmapMilestones[milestoneIndex]
                val updatedMilestone = milestone.copy(isCompleted = isCompleted)

                // Create a new list with the updated milestone
                val updatedList = roadmapMilestones.toMutableList()
                updatedList[milestoneIndex] = updatedMilestone
                allMilestones[roadmapId] = updatedList

                // Update the flow
                _milestonesFlow.value = allMilestones

                // Update roadmap progress
                updateRoadmapProgress(roadmapId)

                // Check if roadmap is completed and send notification if it is
                checkRoadmapCompletion(roadmapId)

                return
            }
        }
    }

    override suspend fun updateMilestoneNote(milestoneId: String, noteContent: String) {
        val updatedNotes = _milestoneNotesFlow.value.toMutableMap()
        updatedNotes[milestoneId] = noteContent
        _milestoneNotesFlow.value = updatedNotes
    }

    override suspend fun getRoadmapTitle(roadmapId: String): String {
        return roadmaps.find { it.id == roadmapId }?.title ?: "Unknown Roadmap"
    }

    // Helper methods
    private fun updateRoadmapProgress(roadmapId: String) {
        val roadmapMilestones = _milestonesFlow.value[roadmapId] ?: return
        val totalMilestones = roadmapMilestones.size
        if (totalMilestones == 0) return

        val completedMilestones = roadmapMilestones.count { it.isCompleted }
        val progress = completedMilestones.toFloat() / totalMilestones

        val updatedRoadmaps = _roadmapsFlow.value.toMutableList()
        val roadmapIndex = updatedRoadmaps.indexOfFirst { it.id == roadmapId }

        if (roadmapIndex != -1) {
            val roadmap = updatedRoadmaps[roadmapIndex]
            updatedRoadmaps[roadmapIndex] = roadmap.copy(progress = progress)
            _roadmapsFlow.value = updatedRoadmaps
        }
    }

    private suspend fun checkRoadmapCompletion(roadmapId: String) {
        val roadmap = _roadmapsFlow.value.find { it.id == roadmapId } ?: return

        // If roadmap is not fully completed or already notified, return
        if (roadmap.progress < 1.0f || completedRoadmapIds.contains(roadmapId)) return

        // Mark as notified
        completedRoadmapIds.add(roadmapId)

        // Add a notification for roadmap completion
        val notification = Notification(
            id = "roadmap_${roadmapId}_${Date().time}",
            title = "Roadmap Completed!",
            message = "Congratulations on completing ${roadmap.title}!",
            timestamp = Date(),
            isRead = false
        )

        // Simulate delay for network operation
        delay(500)

        // We would normally call the notification repository method here
        // This is just a placeholder, you'll need to implement the actual method
        // in your NotificationRepository class
        // notificationRepository.addNotification(notification)
    }
}
