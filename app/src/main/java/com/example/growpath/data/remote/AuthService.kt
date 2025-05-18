package com.example.growpath.data.remote

import com.example.growpath.data.model.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(
    private val firebaseService: FirebaseService
) {
    private val auth = firebaseService.auth

    suspend fun signInWithEmail(email: String, password: String): AuthResult {
        return auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun signUpWithEmail(email: String, password: String, displayName: String): AuthResult {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()
        auth.currentUser?.updateProfile(profileUpdates)?.await()
        return authResult
    }

    suspend fun signInWithGoogle(googleAccount: GoogleSignInAccount): AuthResult {
        val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
        return auth.signInWithCredential(credential).await()
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    fun getCurrentUserDisplayName(): String? {
        return auth.currentUser?.displayName
    }

    fun getCurrentUserPhotoUrl(): String? {
        return auth.currentUser?.photoUrl?.toString()
    }
}
