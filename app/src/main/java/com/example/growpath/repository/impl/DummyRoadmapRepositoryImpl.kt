package com.example.growpath.repository.impl

import com.example.growpath.data.NotificationRepository
import com.example.growpath.model.Milestone
import com.example.growpath.model.Roadmap
import com.example.growpath.repository.RoadmapRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
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
            Milestone("204", "2
        )
    )

    // Store milestone notes - milestoneId to note content
    private val milestoneNotes = mutableMapOf<String, String>()

    // Track completed roadmaps to avoid duplicate notifications
    private val completedRoadmapIds = mutableSetOf<String>()

    // Create MutableStateFlow objects to maintain and emit updated data
}

