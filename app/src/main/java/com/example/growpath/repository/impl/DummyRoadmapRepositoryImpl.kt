package com.example.growpath.repository.impl

import com.example.growpath.data.NotificationRepository
import com.example.growpath.data.UserPreferencesManager
import com.example.growpath.model.Milestone
import com.example.growpath.model.Note
import com.example.growpath.model.Roadmap
import com.example.growpath.repository.RoadmapRepository
import com.example.growpath.repository.UserRepository
import com.example.growpath.screen.Notification
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DummyRoadmapRepositoryImpl @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
    private val userPreferencesManager: UserPreferencesManager
) : RoadmapRepository {
    // Constants for XP rewards
    companion object {
        const val XP_MILESTONE_COMPLETION = 25 // XP yang diberikan saat menyelesaikan milestone
        const val XP_ROADMAP_COMPLETION = 100 // XP yang diberikan saat menyelesaikan roadmap

        // Default roadmaps template data
        private val DEFAULT_ROADMAPS = listOf(
            Roadmap(
                "1",
                "Android Development Journey",
                "Master Android development from scratch to expert level",
                0.0f
            ),
            Roadmap("2", "Jetpack Compose Mastery", "Become a UI expert with Jetpack Compose", 0.0f),
            Roadmap("3", "Kotlin Multiplatform", "Build cross-platform apps with KMM", 0.0f),
            Roadmap(
                "4",
                "Firebase Integration",
                "Learn to integrate Firebase services in your app",
                0.0f
            ),
            Roadmap("5", "Material Design 3", "Create beautiful and intuitive UI with Material 3", 0.0f)
        )

        // Default milestones template data
        private val DEFAULT_MILESTONES = mapOf(
            "1" to listOf(
                Milestone(
                    "101",
                    "1",
                    "Setup Development Environment",
                    "Install Android Studio and configure your workspace",
                    false
                ),
                Milestone(
                    "102",
                    "1",
                    "Kotlin Fundamentals",
                    "Learn basic Kotlin syntax and concepts",
                    false
                ),
                Milestone(
                    "103",
                    "1",
                    "Android Basic UI",
                    "Create layouts and basic UI components",
                    false
                ),
                Milestone(
                    "104",
                    "1",
                    "Activity & Fragment Lifecycle",
                    "Master the Android component lifecycle",
                    false
                ),
                Milestone(
                    "105",
                    "1",
                    "Publishing Your App",
                    "Learn to deploy your app to Google Play Store",
                    false
                )
            ),
            "2" to listOf(
                Milestone(
                    "201",
                    "2",
                    "Composable Functions",
                    "Creating and using composable functions",
                    false
                ),
                Milestone("202", "2", "State Management", "Managing state in Compose apps", false),
                Milestone(
                    "203",
                    "2",
                    "Layouts & UI Hierarchy",
                    "Creating complex layouts with Compose",
                    false
                ),
                Milestone(
                    "204",
                    "2",
                    "Animation & Effects",
                    "Adding animations and visual effects to your UI",
                    false
                )
            ),
            "3" to listOf(
                Milestone(
                    "301",
                    "3",
                    "KMM Setup",
                    "Configure your project for Kotlin Multiplatform Mobile",
                    false
                ),
                Milestone("302", "3", "Shared Code", "Write code that works across platforms", false),
                Milestone(
                    "303",
                    "3",
                    "Platform-Specific APIs",
                    "Access native platform features from shared code",
                    false
                ),
                Milestone("304", "3", "Library Integration", "Use libraries in KMM projects", false),
                Milestone(
                    "305",
                    "3",
                    "KMM Publication",
                    "Package and distribute your KMM library",
                    false
                )
            ),
            "4" to listOf(
                Milestone("401", "4", "Firebase Setup", "Add Firebase to your Android project", false),
                Milestone(
                    "402",
                    "4",
                    "Authentication",
                    "Implement user authentication with Firebase",
                    false
                ),
                Milestone(
                    "403",
                    "4",
                    "Firestore Database",
                    "Store and retrieve data from Firestore",
                    false
                ),
                Milestone(
                    "404",
                    "4",
                    "Cloud Functions",
                    "Create serverless functions with Firebase",
                    false
                ),
                Milestone(
                    "405",
                    "4",
                    "Firebase Analytics",
                    "Track user behavior with Firebase Analytics",
                    false
                )
            ),
            "5" to listOf(
                Milestone(
                    "501",
                    "5",
                    "Material 3 Principles",
                    "Learn the fundamentals of Material Design 3",
                    false
                ),
                Milestone("502", "5", "Color System", "Implement dynamic color theming", false),
                Milestone(
                    "503",
                    "5",
                    "Component Styling",
                    "Style components according to Material 3 guidelines",
                    false
                ),
                Milestone(
                    "504",
                    "5",
                    "Animation Patterns",
                    "Implement Material 3 motion and animation patterns",
                    false
                ),
                Milestone(
                    "505",
                    "5",
                    "Dark Theme",
                    "Support light and dark themes with Material 3",
                    false
                )
            )
        )
    }

    // Per-user roadmaps and milestones data - each user has their own copies
    private val userRoadmaps = mutableMapOf<String?, MutableList<Roadmap>>()
    private val userMilestones = mutableMapOf<String?, MutableMap<String, MutableList<Milestone>>>()
    private val userCompletedRoadmapIds = mutableMapOf<String?, MutableSet<String>>()
    private val userNotes = mutableMapOf<String?, MutableList<Note>>()

    // Create MutableStateFlow objects to maintain and emit updated data
    private val _roadmapsFlow = MutableStateFlow<List<Roadmap>>(emptyList())
    private val _milestonesFlow = MutableStateFlow<Map<String, List<Milestone>>>(emptyMap())
    private val _notesFlow = MutableStateFlow<List<Note>>(emptyList())

    init {
        // Initialize with the current active user
        resetDataForCurrentUser()

        // Launch a coroutine to collect user changes properly
        kotlinx.coroutines.GlobalScope.launch {
            userPreferencesManager.userNameFlow.collect { username ->
                resetDataForCurrentUser()
            }
        }
    }

    // Reset data for the current user
    private fun resetDataForCurrentUser() {
        val currentUser = userPreferencesManager.getCurrentUsername()

        // Initialize roadmaps for this user if not already done
        if (!userRoadmaps.containsKey(currentUser)) {
            userRoadmaps[currentUser] = DEFAULT_ROADMAPS.map { it.copy() }.toMutableList()
        }

        // Initialize milestones for this user if not already done
        if (!userMilestones.containsKey(currentUser)) {
            val userMilestonesCopy = mutableMapOf<String, MutableList<Milestone>>()
            DEFAULT_MILESTONES.forEach { (roadmapId, milestones) ->
                userMilestonesCopy[roadmapId] = milestones.map { it.copy() }.toMutableList()
            }
            userMilestones[currentUser] = userMilestonesCopy
        }

        // Initialize completed roadmaps set for this user if not already done
        if (!userCompletedRoadmapIds.containsKey(currentUser)) {
            userCompletedRoadmapIds[currentUser] = mutableSetOf()
        }

        // Initialize notes for this user if not already done
        if (!userNotes.containsKey(currentUser)) {
            userNotes[currentUser] = mutableListOf()
        }

        // Update the flows with current user's data
        _roadmapsFlow.value = getUserRoadmaps()
        _milestonesFlow.value = getUserMilestones()
        _notesFlow.value = getUserNotes()
    }

    // Helper methods to get current user's data
    private fun getUserRoadmaps(): List<Roadmap> {
        val currentUser = userPreferencesManager.getCurrentUsername()
        return userRoadmaps[currentUser]?.toList() ?: emptyList()
    }

    private fun getUserMilestones(): Map<String, List<Milestone>> {
        val currentUser = userPreferencesManager.getCurrentUsername()
        return userMilestones[currentUser]?.mapValues { it.value.toList() } ?: emptyMap()
    }

    private fun getUserNotes(): List<Note> {
        val currentUser = userPreferencesManager.getCurrentUsername()
        return userNotes[currentUser]?.toList() ?: emptyList()
    }

    private fun getUserCompletedRoadmapIds(): Set<String> {
        val currentUser = userPreferencesManager.getCurrentUsername()
        return userCompletedRoadmapIds[currentUser]?.toSet() ?: emptySet()
    }

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
        val currentUser = userPreferencesManager.getCurrentUsername()
        val userMilestoneMap = userMilestones[currentUser] ?: return
        val allMilestones = _milestonesFlow.value.toMutableMap()
        val previouslyCompleted =
            allMilestones.values.flatten().find { it.id == milestoneId }?.isCompleted ?: false

        // If the milestone is already completed, prevent uncompleting it
        if (previouslyCompleted && !isCompleted) {
            return  // Prevent uncompleting a completed milestone
        }

        // Find and update the milestone
        userMilestoneMap.forEach { (roadmapId, roadmapMilestones) ->
            val milestoneIndex = roadmapMilestones.indexOfFirst { it.id == milestoneId }
            if (milestoneIndex != -1) {
                val milestone = roadmapMilestones[milestoneIndex]
                val updatedMilestone = milestone.copy(isCompleted = isCompleted)

                // Create a new list with the updated milestone
                roadmapMilestones[milestoneIndex] = updatedMilestone

                // Update the flow
                _milestonesFlow.value = getUserMilestones()

                // Update roadmap progress
                updateRoadmapProgress(roadmapId)

                // Check if roadmap is completed and send notification if it is
                checkRoadmapCompletion(roadmapId)

                // Award XP if milestone newly completed
                if (isCompleted && !previouslyCompleted) {
                    // Award XP for milestone completion
                    userRepository.addExperiencePoints(XP_MILESTONE_COMPLETION)

                    try {
                        // Notify achievement system about milestone completion
                        val achievementRepository = userRepository.getAchievementRepository()
                        achievementRepository?.recordMilestoneCompletion(milestoneId)
                    } catch (e: Exception) {
                        // Ignore any errors with achievement tracking
                    }
                }

                return
            }
        }
    }

    override suspend fun updateMilestoneNote(milestoneId: String, noteContent: String) {
        val currentUser = userPreferencesManager.getCurrentUsername()
        // Create a new note with unique id
        val newNote = Note(
            id = UUID.randomUUID().toString(),
            milestoneId = milestoneId,
            content = noteContent,
            createdAt = System.currentTimeMillis()
        )

        // Add to the user's notes list
        val userNotesList = userNotes[currentUser] ?: mutableListOf()
        userNotesList.add(newNote)
        userNotes[currentUser] = userNotesList

        // Update the flow
        _notesFlow.value = getUserNotes()
    }

    override suspend fun updateExistingNote(noteId: String, newContent: String) {
        val currentUser = userPreferencesManager.getCurrentUsername()
        val userNotesList = userNotes[currentUser] ?: return
        val noteIndex = userNotesList.indexOfFirst { it.id == noteId }

        if (noteIndex != -1) {
            // Get the existing note
            val existingNote = userNotesList[noteIndex]

            // Create updated note with new content
            val updatedNote = existingNote.copy(
                content = newContent,
                createdAt = System.currentTimeMillis()
            )

            // Replace the old note
            userNotesList[noteIndex] = updatedNote

            // Update the flow
            _notesFlow.value = getUserNotes()
        }
    }

    override fun getNotesForMilestone(milestoneId: String): Flow<List<Note>> {
        return _notesFlow.map { allNotes ->
            allNotes.filter { it.milestoneId == milestoneId }
                .sortedByDescending { it.createdAt }
        }
    }

    override suspend fun deleteNote(noteId: String) {
        val currentUser = userPreferencesManager.getCurrentUsername()
        val userNotesList = userNotes[currentUser] ?: return
        userNotesList.removeIf { it.id == noteId }

        // Update the flow
        _notesFlow.value = getUserNotes()
    }

    override suspend fun getRoadmapTitle(roadmapId: String): String {
        val currentUser = userPreferencesManager.getCurrentUsername()
        return userRoadmaps[currentUser]?.find { it.id == roadmapId }?.title ?: "Unknown Roadmap"
    }

    override suspend fun markRoadmapAsLastOpened(roadmapId: String) {
        // Save last opened roadmap ID to UserPreferencesManager
        userPreferencesManager.saveLastOpenedRoadmapId(roadmapId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getLastOpenedRoadmap(): Flow<Roadmap?> {
        return userPreferencesManager.lastOpenedRoadmapFlow
            .flatMapLatest { lastOpenedId ->
                if (lastOpenedId != null) {
                    getRoadmapById(lastOpenedId)
                } else {
                    flow { emit(null) }
                }
            }
    }

    // Helper method to update the progress of a roadmap based on completed milestones
    private fun updateRoadmapProgress(roadmapId: String) {
        val currentUser = userPreferencesManager.getCurrentUsername()
        val userRoadmapsList = userRoadmaps[currentUser] ?: return
        val userMilestoneMap = userMilestones[currentUser] ?: return

        val roadmapIndex = userRoadmapsList.indexOfFirst { it.id == roadmapId }
        if (roadmapIndex == -1) return

        val milestonesList = userMilestoneMap[roadmapId] ?: return
        if (milestonesList.isEmpty()) return

        // Calculate progress as the ratio of completed milestones to total milestones
        val completedCount = milestonesList.count { it.isCompleted }
        val progress = completedCount.toFloat() / milestonesList.size

        // Update roadmap with new progress
        val updatedRoadmap = userRoadmapsList[roadmapIndex].copy(progress = progress)
        userRoadmapsList[roadmapIndex] = updatedRoadmap

        // Update the flow
        _roadmapsFlow.value = getUserRoadmaps()
    }

    // Helper method to check if a roadmap has been completed and send a notification
    private suspend fun checkRoadmapCompletion(roadmapId: String) {
        val currentUser = userPreferencesManager.getCurrentUsername()
        val userRoadmapsList = userRoadmaps[currentUser] ?: return
        val userMilestoneMap = userMilestones[currentUser] ?: return
        val userCompletedIds = userCompletedRoadmapIds[currentUser] ?: mutableSetOf()

        val roadmap = userRoadmapsList.find { it.id == roadmapId } ?: return
        val milestonesList = userMilestoneMap[roadmapId] ?: return

        // Check if all milestones are completed
        val allCompleted = milestonesList.isNotEmpty() && milestonesList.all { it.isCompleted }

        if (allCompleted && roadmap.progress < 1.0f) {
            // Update roadmap to 100% progress
            val roadmapIndex = userRoadmapsList.indexOfFirst { it.id == roadmapId }
            val updatedRoadmap = roadmap.copy(progress = 1.0f)
            userRoadmapsList[roadmapIndex] = updatedRoadmap
            _roadmapsFlow.value = getUserRoadmaps()

            // Skip notification if we've already completed this roadmap
            if (userCompletedIds.contains(roadmapId)) {
                return
            }

            // Award XP for completing the roadmap
            userRepository.addExperiencePoints(XP_ROADMAP_COMPLETION)

            // Send a notification
            notificationRepository.addNotification(
                title = "Congratulations!",
                message = "You've completed the \"${roadmap.title}\" roadmap!"
            )

            // Mark this roadmap as completed to avoid duplicate notifications
            userCompletedIds.add(roadmapId)
        }
    }
}
