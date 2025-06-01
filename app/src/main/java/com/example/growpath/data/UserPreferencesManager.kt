package com.example.growpath.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager class for handling persistent user preferences using SharedPreferences
 * This is a simpler implementation to avoid DataStore issues
 * Now supports multiple users
 */
@Singleton
class UserPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

    private val _userNameFlow = MutableStateFlow<String?>(null)
    private val _userEmailFlow = MutableStateFlow<String?>(null)
    private val _userPhotoUrlFlow = MutableStateFlow<String?>(null)
    private val _userThemeFlow = MutableStateFlow<String?>(null)
    private val _userLanguageFlow = MutableStateFlow<String?>(null)
    private val _lastOpenedRoadmapFlow = MutableStateFlow<String?>(null)
    private val _userLevelFlow = MutableStateFlow<Int>(1)
    private val _userXpFlow = MutableStateFlow<Int>(0)

    // Active user
    private var activeUsername: String? = null

    init {
        // Get the currently logged in username
        activeUsername = getCurrentUsername()

        // Initialize flows with saved preferences for the current user
        _userNameFlow.value = getUserName()
        _userEmailFlow.value = getUserEmail()
        _userPhotoUrlFlow.value = getUserPhotoUrl()
        _userThemeFlow.value = getUserTheme()
        _userLanguageFlow.value = getUserLanguage()
        _lastOpenedRoadmapFlow.value = getLastOpenedRoadmapId()
        _userLevelFlow.value = getUserLevel()
        _userXpFlow.value = getUserXP()
    }

    // Keys for preferences
    private object PreferenceKeys {
        const val USER_NAME = "user_name"
        const val USER_EMAIL = "user_email"
        const val USER_PHOTO_URL = "user_photo_url"
        const val USER_THEME = "user_theme"
        const val USER_LANGUAGE = "user_language"
        const val LAST_OPENED_ROADMAP = "last_opened_roadmap"
        const val USER_LEVEL = "user_level"
        const val USER_XP = "user_experience"
        const val ACTIVE_USERNAME = "active_username"
        const val REGISTERED_USERS = "registered_users"
    }

    // Get user name as a Flow
    val userNameFlow: Flow<String?> = _userNameFlow.asStateFlow()

    // Get user email as a Flow
    val userEmailFlow: Flow<String?> = _userEmailFlow.asStateFlow()

    // Get user photo URL as a Flow
    val userPhotoUrlFlow: Flow<String?> = _userPhotoUrlFlow.asStateFlow()

    // Get user theme preference as a Flow
    val userThemeFlow: Flow<String?> = _userThemeFlow.asStateFlow()

    // Get user language preference as a Flow
    val userLanguageFlow: Flow<String?> = _userLanguageFlow.asStateFlow()

    // Get last opened roadmap as a Flow
    val lastOpenedRoadmapFlow: Flow<String?> = _lastOpenedRoadmapFlow.asStateFlow()

    // Get user level and XP as Flows
    val userLevelFlow: Flow<Int> = _userLevelFlow.asStateFlow()
    val userXpFlow: Flow<Int> = _userXpFlow.asStateFlow()

    // Helper function to generate key with username prefix for user-specific data
    private fun getUserSpecificKey(key: String): String {
        return if (activeUsername != null) {
            "${activeUsername}_$key"
        } else {
            key // Fall back to global key if no user is active
        }
    }

    // Get methods that directly access SharedPreferences - changed from private to public
    fun getUserName(): String? =
        sharedPreferences.getString(getUserSpecificKey(PreferenceKeys.USER_NAME), null)

    fun getUserEmail(): String? =
        sharedPreferences.getString(getUserSpecificKey(PreferenceKeys.USER_EMAIL), null)

    fun getUserPhotoUrl(): String? =
        sharedPreferences.getString(getUserSpecificKey(PreferenceKeys.USER_PHOTO_URL), null)

    private fun getUserTheme(): String? =
        sharedPreferences.getString(getUserSpecificKey(PreferenceKeys.USER_THEME), null)

    private fun getUserLanguage(): String? =
        sharedPreferences.getString(getUserSpecificKey(PreferenceKeys.USER_LANGUAGE), null)

    // Get last opened roadmap
    fun getLastOpenedRoadmapId(): String? =
        sharedPreferences.getString(getUserSpecificKey(PreferenceKeys.LAST_OPENED_ROADMAP), null)

    // Get user level
    fun getUserLevel(): Int =
        sharedPreferences.getInt(getUserSpecificKey(PreferenceKeys.USER_LEVEL), 1)

    // Get user XP
    fun getUserXP(): Int =
        sharedPreferences.getInt(getUserSpecificKey(PreferenceKeys.USER_XP), 0)

    // Save methods with user-specific keys
    fun saveUserName(name: String) {
        sharedPreferences.edit()
            .putString(getUserSpecificKey(PreferenceKeys.USER_NAME), name)
            .apply()
        _userNameFlow.value = name
    }

    fun saveUserEmail(email: String) {
        sharedPreferences.edit()
            .putString(getUserSpecificKey(PreferenceKeys.USER_EMAIL), email)
            .apply()
        _userEmailFlow.value = email
    }

    fun saveUserPhotoUrl(url: String?) {
        sharedPreferences.edit()
            .putString(getUserSpecificKey(PreferenceKeys.USER_PHOTO_URL), url)
            .apply()
        _userPhotoUrlFlow.value = url
    }

    fun saveUserTheme(theme: String) {
        sharedPreferences.edit()
            .putString(getUserSpecificKey(PreferenceKeys.USER_THEME), theme)
            .apply()
        _userThemeFlow.value = theme
    }

    fun saveUserLanguage(language: String) {
        sharedPreferences.edit()
            .putString(getUserSpecificKey(PreferenceKeys.USER_LANGUAGE), language)
            .apply()
        _userLanguageFlow.value = language
    }

    fun saveLastOpenedRoadmapId(id: String) {
        sharedPreferences.edit()
            .putString(getUserSpecificKey(PreferenceKeys.LAST_OPENED_ROADMAP), id)
            .apply()
        _lastOpenedRoadmapFlow.value = id
    }

    fun saveUserLevel(level: Int) {
        sharedPreferences.edit()
            .putInt(getUserSpecificKey(PreferenceKeys.USER_LEVEL), level)
            .apply()
        _userLevelFlow.value = level
    }

    fun saveUserXP(xp: Int) {
        sharedPreferences.edit()
            .putInt(getUserSpecificKey(PreferenceKeys.USER_XP), xp)
            .apply()
        _userXpFlow.value = xp
    }

    // ===== User Authentication Methods =====

    // Set active user and update all flows
    fun setActiveUser(username: String) {
        activeUsername = username
        sharedPreferences.edit()
            .putString(PreferenceKeys.ACTIVE_USERNAME, username)
            .apply()

        // Refresh all flows with new user's data
        _userNameFlow.value = getUserName()
        _userEmailFlow.value = getUserEmail()
        _userPhotoUrlFlow.value = getUserPhotoUrl()
        _userThemeFlow.value = getUserTheme()
        _userLanguageFlow.value = getUserLanguage()
        _lastOpenedRoadmapFlow.value = getLastOpenedRoadmapId()
        _userLevelFlow.value = getUserLevel()
        _userXpFlow.value = getUserXP()

        Log.d("UserPreferences", "Active user set to: $username")
    }

    // Clear active user on logout
    fun clearActiveUser() {
        sharedPreferences.edit()
            .remove(PreferenceKeys.ACTIVE_USERNAME)
            .apply()
        activeUsername = null

        // Reset all flows
        _userNameFlow.value = null
        _userEmailFlow.value = null
        _userPhotoUrlFlow.value = null
        _userThemeFlow.value = null
        _userLanguageFlow.value = null
        _lastOpenedRoadmapFlow.value = null
        _userLevelFlow.value = 1
        _userXpFlow.value = 0

        Log.d("UserPreferences", "Active user cleared")
    }

    // Get the current active username
    fun getCurrentUsername(): String? =
        sharedPreferences.getString(PreferenceKeys.ACTIVE_USERNAME, null)

    // Save user credentials for a new user
    fun saveUserCredentials(username: String, password: String): Boolean {
        try {
            // Check if username already exists
            if (isUserRegistered(username)) {
                return false
            }

            // Get the registered users JSON array
            val usersJsonString = sharedPreferences.getString(PreferenceKeys.REGISTERED_USERS, "[]")
            val usersArray = JSONArray(usersJsonString)

            // Create a new user JSON object
            val userObj = JSONObject().apply {
                put("username", username)
                put("password", password)
            }

            // Add the new user and save back to preferences
            usersArray.put(userObj)
            sharedPreferences.edit()
                .putString(PreferenceKeys.REGISTERED_USERS, usersArray.toString())
                .apply()

            Log.d("UserPreferences", "User registered: $username")
            return true
        } catch (e: Exception) {
            Log.e("UserPreferences", "Error saving user credentials: ${e.message}")
            return false
        }
    }

    // Check if a user exists and the password is correct
    fun validateUserCredentials(username: String, password: String): Boolean {
        try {
            val usersJsonString = sharedPreferences.getString(PreferenceKeys.REGISTERED_USERS, "[]")
            val usersArray = JSONArray(usersJsonString)

            for (i in 0 until usersArray.length()) {
                val userObj = usersArray.getJSONObject(i)
                if (userObj.getString("username") == username &&
                    userObj.getString("password") == password) {
                    return true
                }
            }
            return false
        } catch (e: Exception) {
            Log.e("UserPreferences", "Error validating credentials: ${e.message}")
            return false
        }
    }

    // Check if a username is already registered
    fun isUserRegistered(username: String): Boolean {
        try {
            val usersJsonString = sharedPreferences.getString(PreferenceKeys.REGISTERED_USERS, "[]")
            val usersArray = JSONArray(usersJsonString)

            for (i in 0 until usersArray.length()) {
                val userObj = usersArray.getJSONObject(i)
                if (userObj.getString("username") == username) {
                    return true
                }
            }
            return false
        } catch (e: Exception) {
            Log.e("UserPreferences", "Error checking if user exists: ${e.message}")
            return false
        }
    }

    // Update username
    fun updateUsername(oldUsername: String, newUsername: String, password: String): Boolean {
        try {
            // Check if the new username already exists (not the same as old one)
            if (oldUsername != newUsername && isUserRegistered(newUsername)) {
                return false
            }

            val usersJsonString = sharedPreferences.getString(PreferenceKeys.REGISTERED_USERS, "[]")
            val usersArray = JSONArray(usersJsonString)

            for (i in 0 until usersArray.length()) {
                val userObj = usersArray.getJSONObject(i)
                if (userObj.getString("username") == oldUsername &&
                    userObj.getString("password") == password) {

                    // Update username in the JSON array
                    userObj.put("username", newUsername)
                    usersArray.put(i, userObj)

                    // Save the updated array
                    sharedPreferences.edit()
                        .putString(PreferenceKeys.REGISTERED_USERS, usersArray.toString())
                        .apply()

                    // Update current active username if it's the same
                    if (getCurrentUsername() == oldUsername) {
                        setActiveUser(newUsername)
                    }

                    return true
                }
            }
            return false
        } catch (e: Exception) {
            Log.e("UserPreferences", "Error updating username: ${e.message}")
            return false
        }
    }

    // Update password
    fun updatePassword(username: String, oldPassword: String, newPassword: String): Boolean {
        try {
            val usersJsonString = sharedPreferences.getString(PreferenceKeys.REGISTERED_USERS, "[]")
            val usersArray = JSONArray(usersJsonString)

            for (i in 0 until usersArray.length()) {
                val userObj = usersArray.getJSONObject(i)
                if (userObj.getString("username") == username &&
                    userObj.getString("password") == oldPassword) {

                    // Update password
                    userObj.put("password", newPassword)
                    usersArray.put(i, userObj)

                    // Save the updated array
                    sharedPreferences.edit()
                        .putString(PreferenceKeys.REGISTERED_USERS, usersArray.toString())
                        .apply()

                    return true
                }
            }
            return false
        } catch (e: Exception) {
            Log.e("UserPreferences", "Error updating password: ${e.message}")
            return false
        }
    }

    // Get list of registered usernames (for testing/debug)
    fun getRegisteredUsernames(): List<String> {
        val usernames = mutableListOf<String>()
        try {
            val usersJsonString = sharedPreferences.getString(PreferenceKeys.REGISTERED_USERS, "[]")
            val usersArray = JSONArray(usersJsonString)

            for (i in 0 until usersArray.length()) {
                val userObj = usersArray.getJSONObject(i)
                usernames.add(userObj.getString("username"))
            }
        } catch (e: Exception) {
            Log.e("UserPreferences", "Error getting usernames: ${e.message}")
        }
        return usernames
    }
}
