package com.example.growpath.data.remote

import android.net.Uri
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageService @Inject constructor(
    private val firebaseService: FirebaseService
) {
    private val storage = firebaseService.storage

    suspend fun uploadUserProfileImage(userId: String, imageUri: Uri): String {
        val fileRef = storage.reference.child("profile_images/$userId/${UUID.randomUUID()}")
        return uploadImage(fileRef, imageUri)
    }

    suspend fun uploadRoadmapImage(roadmapId: String, imageUri: Uri): String {
        val fileRef = storage.reference.child("roadmap_images/$roadmapId/${UUID.randomUUID()}")
        return uploadImage(fileRef, imageUri)
    }

    suspend fun uploadMilestoneImage(milestoneId: String, imageUri: Uri): String {
        val fileRef = storage.reference.child("milestone_images/$milestoneId/${UUID.randomUUID()}")
        return uploadImage(fileRef, imageUri)
    }

    suspend fun uploadAchievementIcon(achievementId: String, imageUri: Uri): String {
        val fileRef = storage.reference.child("achievement_icons/$achievementId/${UUID.randomUUID()}")
        return uploadImage(fileRef, imageUri)
    }

    private suspend fun uploadImage(fileRef: StorageReference, imageUri: Uri): String {
        fileRef.putFile(imageUri).await()
        return fileRef.downloadUrl.await().toString()
    }

    suspend fun deleteFile(fileUrl: String) {
        try {
            storage.getReferenceFromUrl(fileUrl).delete().await()
        } catch (e: Exception) {
            // Handle error or just ignore if file doesn't exist
        }
    }
}
