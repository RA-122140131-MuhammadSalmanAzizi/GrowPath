package com.example.growpath.repository.impl

import com.example.growpath.model.Milestone
import com.example.growpath.model.Roadmap
import com.example.growpath.repository.RoadmapRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DummyRoadmapRepositoryImpl : RoadmapRepository {
    // Make roadmaps and milestones mutable
    private val roadmaps = mutableListOf(
        Roadmap("1", "Android Development Journey", "Master Android development from scratch to expert level", 0.35f),
        Roadmap("2", "Jetpack Compose Mastery", "Become a UI expert with Jetpack Compose", 0.75f),
        Roadmap("3", "Kotlin Multiplatform", "Build cross-platform apps with KMM", 0.1f),
        Roadmap("4", "Firebase Integration", "Learn to integrate Firebase services in your app", 1.0f),
        Roadmap("5", "Material Design 3", "Create beautiful and intuitive UI with Material 3", 0.0f)
    )

    private val milestones = mutableMapOf(
        "1" to mutableListOf( // Make inner lists mutable
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
            Milestone("204", "2", "Animations & Transitions", "Add life to your UI with animations", false),
            Milestone("205", "2", "Performance Optimization", "Optimize your Compose app performance", false)
        ),
        "3" to mutableListOf(
            Milestone("301", "3", "Setup KMM", "Setup Kotlin Multiplatform Mobile", true),
            Milestone("302", "3", "Shared Code", "Write shared code for iOS and Android", true),
            Milestone("303", "3", "Platform-Specific Code", "Write platform-specific code for iOS and Android", false)
        ),
        "4" to mutableListOf(
            Milestone("401", "4", "Firebase Authentication", "Implement Firebase Authentication", true),
            Milestone("402", "4", "Firebase Firestore", "Use Firebase Firestore for data storage", true),
            Milestone("403", "4", "Firebase Cloud Messaging", "Implement push notifications with Firebase Cloud Messaging", false)
        ),
        "5" to mutableListOf(
            Milestone("501", "5", "Material 3 Basics", "Learn the basics of Material Design 3", true),
            Milestone("502", "5", "Material 3 Components", "Use Material Design 3 components in your app", false)
        )
    )
    // Store milestone notes - milestoneId to note content
    private val milestoneNotes = mutableMapOf<String, String>()

    override fun getRoadmaps(): Flow<List<Roadmap>> { // Changed to return Flow and non-suspend
        // Recalculate progress for all roadmaps before emitting them in the flow
        val currentRoadmaps = roadmaps.map { roadmap ->
            val roadmapMilestones = milestones[roadmap.id] ?: emptyList()
            if (roadmapMilestones.isNotEmpty()) {
                val completedCount = roadmapMilestones.count { it.isCompleted }
                val newProgress = completedCount.toFloat() / roadmapMilestones.size
                roadmap.copy(progress = newProgress)
            } else {
                if (roadmap.progress != 1.0f) {
                    roadmap.copy(progress = 0.0f)
                } else {
                    roadmap
                }
            }
        }
        return flowOf(currentRoadmaps.toList()) // Emit the current list once as a Flow
    }

    override suspend fun getRoadmapTitle(roadmapId: String): String {
        delay(10) // Simulate network delay
        return roadmaps.find { it.id == roadmapId }?.title ?: "Unknown Roadmap"
    }

    override suspend fun getMilestonesForRoadmap(roadmapId: String): List<Milestone> {
        delay(10) // Simulate network delay
        return milestones[roadmapId]?.map { milestone ->
            milestone.copy(note = milestoneNotes[milestone.id])
        } ?: emptyList()
    }

    override suspend fun getMilestoneById(milestoneId: String): Milestone? {
        delay(10) // Simulate network delay
        val milestone = milestones.values.flatten().find { it.id == milestoneId }
        return milestone?.copy(note = milestoneNotes[milestoneId])
    }

    override suspend fun updateMilestoneCompletion(milestoneId: String, isCompleted: Boolean) {
        delay(10) // Simulate network delay
        var roadmapToUpdateId: String? = null
        milestones.forEach { (rId, mList) ->
            val milestoneIndex = mList.indexOfFirst { it.id == milestoneId }
            if (milestoneIndex != -1) {
                mList[milestoneIndex] = mList[milestoneIndex].copy(isCompleted = isCompleted)
                roadmapToUpdateId = rId
                return@forEach
            }
        }

        roadmapToUpdateId?.let { rId ->
            val roadmapIndex = roadmaps.indexOfFirst { it.id == rId }
            if (roadmapIndex != -1) {
                val currentRoadmap = roadmaps[roadmapIndex]
                val roadmapMilestones = milestones[rId] ?: emptyList()
                if (roadmapMilestones.isNotEmpty()) {
                    val completedCount = roadmapMilestones.count { it.isCompleted }
                    val newProgress = completedCount.toFloat() / roadmapMilestones.size
                    roadmaps[roadmapIndex] = currentRoadmap.copy(progress = newProgress)
                } else {
                    roadmaps[roadmapIndex] = currentRoadmap.copy(progress = if (currentRoadmap.progress == 1.0f && isCompleted) 1.0f else 0.0f)
                }
            }
        }
    }

    override suspend fun updateMilestoneNote(milestoneId: String, noteContent: String) {
        delay(10)
        milestoneNotes[milestoneId] = noteContent
    }
}
