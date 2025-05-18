package com.example.growpath.data.repository

import android.net.Uri
import com.example.growpath.data.local.UserDao
import com.example.growpath.data.model.User
import com.example.growpath.data.remote.AuthService
import com.example.growpath.data.remote.FirestoreService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val authService: AuthService,
    private val firestoreService: FirestoreService
) : UserRepository {

    override fun getCurrentUser(): Flow<User?> {
        val userId = authService.getCurrentUserId() ?: return kotlinx.coroutines.flow.flowOf(null)
        return userDao.getUserById(userId)
    }

    override fun getUserById(userId: String): Flow<User?> {
        return userDao.getUserById(userId)
    }

    override suspend fun createUser(user: User) {
        userDao.insertUser(user)
        firestoreService.createUser(user)
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user)
        firestoreService.updateUser(user)
    }

    override suspend fun addExperience(userId: String, amount: Int) {
        userDao.addExperience(userId, amount)
        // Update Firestore
        val user = userDao.getUserById(userId).first()
        user?.let { firestoreService.updateUser(it) }
    }

    override suspend fun updateLevel(userId: String, newLevel: Int) {
        userDao.updateLevel(userId, newLevel)
        // Update Firestore
        val user = userDao.getUserById(userId).first()
        user?.let { firestoreService.updateUser(it) }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val authResult = authService.signInWithEmail(email, password)
            val userId = authResult.user?.uid ?: return Result.failure(Exception("User ID not found"))

            val firebaseUser = authService.auth.currentUser

            // Check if user exists in Firestore
            val user = firestoreService.getUserById(userId).first()

            if (user == null) {
                // Create user if not exists
                val newUser = User(
                    id = userId,
                    email = email,
                    displayName = firebaseUser?.displayName ?: email.substringBefore('@'),
                    photoUrl = firebaseUser?.photoUrl?.toString()
                )
                createUser(newUser)
                Result.success(newUser)
            } else {
                // Update user in local database
                userDao.insertUser(user)
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<User> {
        return try {
            val authResult = authService.signUpWithEmail(email, password, displayName)
            val userId = authResult.user?.uid ?: return Result.failure(Exception("User ID not found"))

            val user = User(
                id = userId,
                email = email,
                displayName = displayName
            )

            createUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val googleSignInAccount = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(
                com.google.android.gms.tasks.Tasks.await(
                    com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(
                        android.content.Intent()
                    )
                )
            ).result

            val authResult = authService.signInWithGoogle(googleSignInAccount!!)
            val userId = authResult.user?.uid ?: return Result.failure(Exception("User ID not found"))

            val firebaseUser = authService.auth.currentUser

            // Check if user exists in Firestore
            val user = firestoreService.getUserById(userId).first()

            if (user == null) {
                // Create user if not exists
                val newUser = User(
                    id = userId,
                    email = firebaseUser?.email ?: "",
                    displayName = firebaseUser?.displayName ?: "User",
                    photoUrl = firebaseUser?.photoUrl?.toString()
                )
                createUser(newUser)
                Result.success(newUser)
            } else {
                // Update user in local database
                userDao.insertUser(user)
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        authService.signOut()
    }

    override fun isUserLoggedIn(): Boolean {
        return authService.isUserLoggedIn()
    }

    override fun getCurrentUserId(): String? {
        return authService.getCurrentUserId()
    }
}
