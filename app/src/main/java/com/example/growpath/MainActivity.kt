package com.example.growpath

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.growpath.presentation.navigation.NavGraph
import com.example.growpath.presentation.navigation.NavigationHost
import com.example.growpath.presentation.theme.GrowPathTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GrowPathTheme {
                val navController = rememberNavController()
                var startDestination by remember { mutableStateOf(NavGraph.AUTH_LOGIN) }

                // This would typically check if the user is already logged in
                LaunchedEffect(Unit) {
                    // Example: Check for existing user session and update startDestination if needed
                    // If user is logged in, start with dashboard, otherwise auth screen
                    // startDestination = if (userLoggedIn) NavGraph.DASHBOARD else NavGraph.AUTH_LOGIN
                }

                NavigationHost(
                    navController = navController,
                    startDestination = startDestination
                )
            }
        }
    }
}

