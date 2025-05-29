package com.example.growpath.screen

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    // Hardcoded credentials - stored in the ViewModel
    private val validUsername = "Amor"
    private val validPassword = "123"

    // Mendapatkan context aplikasi
    fun getContext(): Context = context

    // Validate credentials
    fun validateCredentials(username: String, password: String): Boolean {
        return username == validUsername && password == validPassword
    }
}

