package com.example.growpath.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager class for handling persistent user preferences using SharedPreferences
 * This is a simpler implementation to avoid DataStore issues
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

    init {
        // Initialize flows with saved preferences
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
        const val LOGIN_USERNAME = "login_username"
        const val LOGIN_PASSWORD = "login_password"
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

    // Get methods that directly access SharedPreferences
    private fun getUserName(): String? =
        sharedPreferences.getString(PreferenceKeys.USER_NAME, null)

    private fun getUserEmail(): String? =
        sharedPreferences.getString(PreferenceKeys.USER_EMAIL, null)

    private fun getUserPhotoUrl(): String? =
        sharedPreferences.getString(PreferenceKeys.USER_PHOTO_URL, null)

    private fun getUserTheme(): String? =
        sharedPreferences.getString(PreferenceKeys.USER_THEME, null)

    private fun getUserLanguage(): String? =
        sharedPreferences.getString(PreferenceKeys.USER_LANGUAGE, null)

    // Get last opened roadmap
    fun getLastOpenedRoadmapId(): String? {
        return sharedPreferences.getString(PreferenceKeys.LAST_OPENED_ROADMAP, null)
    }

    // Get methods for user level and XP
    fun getUserLevel(): Int =
        sharedPreferences.getInt(PreferenceKeys.USER_LEVEL, 1)

    fun getUserXP(): Int =
        sharedPreferences.getInt(PreferenceKeys.USER_XP, 0)

    // Save user name
    suspend fun saveUserName(name: String) {
        sharedPreferences.edit().putString(PreferenceKeys.USER_NAME, name).apply()
        _userNameFlow.value = name
    }

    // Save user email
    suspend fun saveUserEmail(email: String) {
        sharedPreferences.edit().putString(PreferenceKeys.USER_EMAIL, email).apply()
        _userEmailFlow.value = email
    }

    // Save user photo URL
    suspend fun saveUserPhotoUrl(photoUrl: String?) {
        if (photoUrl != null) {
            sharedPreferences.edit().putString(PreferenceKeys.USER_PHOTO_URL, photoUrl).apply()
        } else {
            sharedPreferences.edit().remove(PreferenceKeys.USER_PHOTO_URL).apply()
        }
        _userPhotoUrlFlow.value = photoUrl
    }

    // Save user theme preference
    suspend fun saveUserTheme(theme: String) {
        sharedPreferences.edit().putString(PreferenceKeys.USER_THEME, theme).apply()
        _userThemeFlow.value = theme
    }

    // Save user language preference
    suspend fun saveUserLanguage(language: String) {
        sharedPreferences.edit().putString(PreferenceKeys.USER_LANGUAGE, language).apply()
        _userLanguageFlow.value = language
    }

    // Save last opened roadmap
    fun saveLastOpenedRoadmapId(roadmapId: String) {
        sharedPreferences.edit()
            .putString(PreferenceKeys.LAST_OPENED_ROADMAP, roadmapId)
            .apply()
        _lastOpenedRoadmapFlow.value = roadmapId
    }

    // Save methods for user level and XP
    fun saveUserLevel(level: Int) {
        sharedPreferences.edit().putInt(PreferenceKeys.USER_LEVEL, level).apply()
        _userLevelFlow.value = level
    }

    fun saveUserXP(xp: Int) {
        sharedPreferences.edit().putInt(PreferenceKeys.USER_XP, xp).apply()
        _userXpFlow.value = xp
    }

    // Clear all preferences (for logout)
    suspend fun clearUserPreferences() {
        sharedPreferences.edit().clear().apply()
        _userNameFlow.value = null
        _userEmailFlow.value = null
        _userPhotoUrlFlow.value = null
        _userThemeFlow.value = null
        _userLanguageFlow.value = null
        _lastOpenedRoadmapFlow.value = null
        _userLevelFlow.value = 1
        _userXpFlow.value = 0
    }

    // Authentication methods
    fun getLoginUsername(): String? =
        sharedPreferences.getString(PreferenceKeys.LOGIN_USERNAME, "Amor")

    fun getLoginPassword(): String? =
        sharedPreferences.getString(PreferenceKeys.LOGIN_PASSWORD, "123")

    fun saveLoginCredentials(username: String, password: String) {
        sharedPreferences.edit()
            .putString(PreferenceKeys.LOGIN_USERNAME, username)
            .putString(PreferenceKeys.LOGIN_PASSWORD, password)
            .apply()
    }

    // Method untuk verifikasi kredensial
    fun verifyCredentials(username: String, password: String): Boolean {
        val savedUsername = getLoginUsername()
        val savedPassword = getLoginPassword()
        return username == savedUsername && password == savedPassword
    }
}
