package com.example.growpath.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavigationHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = NavGraph.AUTH_LOGIN) {
            // Ini akan menampilkan placeholder sementara
            androidx.compose.material3.Text("Login Screen - Coming Soon")
        }

        composable(route = NavGraph.DASHBOARD) {
            // Ini akan menampilkan placeholder sementara
            androidx.compose.material3.Text("Dashboard Screen - Coming Soon")
        }
    }
}
