package com.example.growpath.presentation.utils

import android.text.TextUtils
import android.util.Patterns
import java.util.regex.Pattern

object ValidationUtils {

    private const val PASSWORD_MIN_LENGTH = 6
    private val PASSWORD_PATTERN = Pattern.compile(
        "^" +                // Start of string
        "(?=.*[0-9])" +      // At least 1 digit
        "(?=.*[a-zA-Z])" +   // At least 1 letter
        ".{$PASSWORD_MIN_LENGTH,}" +  // At least 6 characters
        "$"                  // End of string
    )

    fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return !TextUtils.isEmpty(password) && PASSWORD_PATTERN.matcher(password).matches()
    }

    fun isValidDisplayName(name: String): Boolean {
        return !TextUtils.isEmpty(name) && name.length >= 3
    }

    fun getPasswordErrorMessage(password: String): String? {
        return when {
            TextUtils.isEmpty(password) -> "Password cannot be empty"
            password.length < PASSWORD_MIN_LENGTH -> "Password must be at least $PASSWORD_MIN_LENGTH characters"
            !password.any { it.isDigit() } -> "Password must contain at least one digit"
            !password.any { it.isLetter() } -> "Password must contain at least one letter"
            else -> null
        }
    }

    fun getEmailErrorMessage(email: String): String? {
        return when {
            TextUtils.isEmpty(email) -> "Email cannot be empty"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            else -> null
        }
    }

    fun getDisplayNameErrorMessage(name: String): String? {
        return when {
            TextUtils.isEmpty(name) -> "Name cannot be empty"
            name.length < 3 -> "Name must be at least 3 characters"
            else -> null
        }
    }
}
