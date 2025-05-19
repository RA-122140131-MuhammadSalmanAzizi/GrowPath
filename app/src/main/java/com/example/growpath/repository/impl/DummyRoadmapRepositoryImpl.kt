package com.example.growpath.repository.impl

import com.example.growpath.model.Milestone
import com.example.growpath.model.Roadmap
import com.example.growpath.repository.RoadmapRepository
import kotlinx.coroutines.delay

class DummyRoadmapRepositoryImpl : RoadmapRepository {
    private val roadmaps = listOf(
        Roadmap("1", "Android Development Journey", "Master Android development from scratch to expert level", 0.35f),
        Roadmap("2", "Jetpack Compose Mastery", "Become a UI expert with Jetpack Compose", 0.75f),
        Roadmap("3", "Kotlin Multiplatform", "Build cross-platform apps with KMM", 0.1f),
        Roadmap("4", "Firebase Integration", "Learn to integrate Firebase services in your app", 1.0f),
        Roadmap("5", "Material Design 3", "Create beautiful and intuitive UI with Material 3", 0.0f)
    )

    private val milestones = mapOf(
        "1" to listOf(
            Milestone("101", "1", "Setup Development Environment", "Install Android Studio and configure your workspace", true),
            Milestone("102", "1", "Kotlin Fundamentals", "Learn basic Kotlin syntax and concepts", true),
            Milestone("103", "1", "Android Basic UI", "Create layouts and basic UI components", false),
            Milestone("104", "1", "Activity & Fragment Lifecycle", "Master the Android component lifecycle", false),
            Milestone("105", "1", "Publishing Your App", "Learn to deploy your app to Google Play Store", false)
        ),
        "2" to listOf(
            Milestone("201", "2", "Composable Functions", "Creating and using composable functions", true),
            Milestone("202", "2", "State Management", "Managing state in Compose apps", true),
            Milestone("203", "2", "Layouts & UI Hierarchy", "Creating complex layouts with Compose", true),
            Milestone("204", "2", "Animations & Transitions", "Add life to your UI with animations", false),
            Milestone("205", "2", "Performance Optimization", "Optimize your Compose app performance", false)
        ),
        "3" to listOf(
            Milestone("301", "3", "KMM Setup", "Configure your development environment for KMM", true),
            Milestone("302", "3", "Shared Code Design", "Design strategies for shared code", false),
            Milestone("303", "3", "iOS Integration", "Integrate KMM with Swift/iOS", false),
            Milestone("304", "3", "Networking & Data Storage", "Handle cross-platform data operations", false)
        ),
        "4" to listOf(
            Milestone("401", "4", "Firebase Authentication", "Implement user authentication", true),
            Milestone("402", "4", "Firestore Database", "Store and retrieve data from Firestore", true),
            Milestone("403", "4", "Firebase Storage", "Upload and manage files", true),
            Milestone("404", "4", "Firebase Analytics", "Track user behavior", true),
            Milestone("405", "4", "Firebase Cloud Messaging", "Implement push notifications", true)
        ),
        "5" to listOf(
            Milestone("501", "5", "Material Design Principles", "Learn the core principles of Material Design", false),
            Milestone("502", "5", "Color System", "Implement effective color schemes", false),
            Milestone("503", "5", "Typography", "Create consistent text styling", false),
            Milestone("504", "5", "Motion & Animation", "Add meaningful motion to your app", false)
        )
    )

    // Store milestone notes - milestoneId to note content
    private val milestoneNotes = mutableMapOf<String, String>()

    override fun getRoadmaps(): List<Roadmap> {
        return roadmaps
    }

    override suspend fun getRoadmapTitle(roadmapId: String): String {
        delay(500) // Simulate network delay
        return roadmaps.find { it.id == roadmapId }?.title ?: "Unknown Roadmap"
    }

    override suspend fun getMilestonesForRoadmap(roadmapId: String): List<Milestone> {
        delay(800) // Simulate network delay
        return milestones[roadmapId] ?: emptyList()
    }

    override suspend fun getMilestoneById(milestoneId: String): Milestone? {
        delay(300) // Simulate network delay
        val milestone = milestones.values.flatten().find { it.id == milestoneId }
        return milestone?.copy(note = milestoneNotes[milestoneId])
    }

    override suspend fun updateMilestoneCompletion(milestoneId: String, isCompleted: Boolean) {
        delay(400) // Simulate network delay
        // In a real implementation, this would update a database or remote source
    }

    override suspend fun updateMilestoneNote(milestoneId: String, noteContent: String) {
        delay(300) // Simulate network delay
        milestoneNotes[milestoneId] = noteContent
    }
}
