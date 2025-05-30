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

    init {
        // Initialize flows with saved preferences
        _userNameFlow.value = getUserName()
        _userEmailFlow.value = getUserEmail()
        _userPhotoUrlFlow.value = getUserPhotoUrl()
        _userThemeFlow.value = getUserTheme()
        _userLanguageFlow.value = getUserLanguage()
    }

    // Keys for preferences
    private object PreferenceKeys {
        const val USER_NAME = "user_name"
        const val USER_EMAIL = "user_email"
        const val USER_PHOTO_URL = "user_photo_url"
        const val USER_THEME = "user_theme"
        const val USER_LANGUAGE = "user_language"
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

    // Clear all preferences (for logout)
    suspend fun clearUserPreferences() {
        sharedPreferences.edit().clear().apply()
        _userNameFlow.value = null
        _userEmailFlow.value = null
        _userPhotoUrlFlow.value = null
        _userThemeFlow.value = null
        _userLanguageFlow.value = null
    }
}
