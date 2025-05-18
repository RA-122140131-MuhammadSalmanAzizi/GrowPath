package com.example.growpath.data.remote

import com.example.growpath.data.model.*
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService @Inject constructor(
    private val firebaseService: FirebaseService
) {
    private val firestore = firebaseService.firestore

    // User operations
    suspend fun createUser(user: User) {
        firestore.collection("users").document(user.id).set(user).await()
    }

    suspend fun updateUser(user: User) {
        firestore.collection("users").document(user.id).set(user).await()
    }

    fun getUserById(userId: String): Flow<User?> = callbackFlow {
        val listenerRegistration = firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    trySend(user)
                } else {
                    trySend(null)
                }
            }
        awaitClose { listenerRegistration.remove() }
    }

    // Roadmap operations
    suspend fun createRoadmap(roadmap: Roadmap) {
        firestore.collection("roadmaps").document(roadmap.id).set(roadmap).await()
    }

    suspend fun updateRoadmap(roadmap: Roadmap) {
        firestore.collection("roadmaps").document(roadmap.id).set(roadmap).await()
    }

    suspend fun deleteRoadmap(roadmapId: String) {
        firestore.collection("roadmaps").document(roadmapId).delete().await()
    }

    fun getRoadmapsByUserId(userId: String): Flow<List<Roadmap>> = callbackFlow {
        val listenerRegistration = firestore.collection("roadmaps")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val roadmaps = snapshot.documents.mapNotNull { it.toObject(Roadmap::class.java) }
                    trySend(roadmaps)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listenerRegistration.remove() }
    }

    // Milestone operations
    suspend fun createMilestone(milestone: Milestone) {
        firestore.collection("milestones").document(milestone.id).set(milestone).await()
    }

    suspend fun updateMilestone(milestone: Milestone) {
        firestore.collection("milestones").document(milestone.id).set(milestone).await()
    }

    suspend fun deleteMilestone(milestoneId: String) {
        firestore.collection("milestones").document(milestoneId).delete().await()
    }

    fun getMilestonesByRoadmapId(roadmapId: String): Flow<List<Milestone>> = callbackFlow {
        val listenerRegistration = firestore.collection("milestones")
            .whereEqualTo("roadmapId", roadmapId)
            .orderBy("position", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val milestones = snapshot.documents.mapNotNull { it.toObject(Milestone::class.java) }
                    trySend(milestones)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listenerRegistration.remove() }
    }

    // Note operations
    suspend fun createNote(note: Note) {
        firestore.collection("notes").document(note.id).set(note).await()
    }

    suspend fun updateNote(note: Note) {
        firestore.collection("notes").document(note.id).set(note).await()
    }

    suspend fun deleteNote(noteId: String) {
        firestore.collection("notes").document(noteId).delete().await()
    }

    fun getNotesByMilestoneId(milestoneId: String): Flow<List<Note>> = callbackFlow {
        val listenerRegistration = firestore.collection("notes")
            .whereEqualTo("milestoneId", milestoneId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val notes = snapshot.documents.mapNotNull { it.toObject(Note::class.java) }
                    trySend(notes)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listenerRegistration.remove() }
    }

    // Achievement operations
    suspend fun createAchievement(achievement: Achievement) {
        firestore.collection("achievements").document(achievement.id).set(achievement).await()
    }

    suspend fun updateAchievement(achievement: Achievement) {
        firestore.collection("achievements").document(achievement.id).set(achievement).await()
    }

    fun getAllAchievements(): Flow<List<Achievement>> = callbackFlow {
        val listenerRegistration = firestore.collection("achievements")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val achievements = snapshot.documents.mapNotNull { it.toObject(Achievement::class.java) }
                    trySend(achievements)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listenerRegistration.remove() }
    }
}
