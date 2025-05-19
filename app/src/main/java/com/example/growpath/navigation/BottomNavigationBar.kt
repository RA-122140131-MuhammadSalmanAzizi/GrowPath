package com.example.growpath.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

// Representing each screen with its route, title, and icons
data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

// List of navigation items for the bottom bar
val bottomNavItems = listOf(
    BottomNavItem(
        route = NavGraph.DASHBOARD,
        title = "Dashboard",
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard
    ),
    BottomNavItem(
        route = NavGraph.ACHIEVEMENTS,
        title = "Achievements",
        selectedIcon = Icons.Filled.Star,
        unselectedIcon = Icons.Outlined.Star
    ),
    BottomNavItem(
        route = NavGraph.EXPLORE,
        title = "Explore",
        selectedIcon = Icons.Filled.Timeline,
        unselectedIcon = Icons.Outlined.Timeline
    ),
    BottomNavItem(
        route = NavGraph.PROFILE,
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Only show the bottom bar on main screens (not on detail screens like Roadmap or Milestone)
    val isMainScreen = currentDestination?.route?.let { route ->
        // Check if the current destination is one of our main screens
        bottomNavItems.any { it.route == route }
    } ?: false

    if (isMainScreen) {
        NavigationBar {
            bottomNavItems.forEach { item ->
                val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    },
                    label = { Text(item.title) },
                    selected = selected,
                    onClick = {
                        if (currentDestination?.route != item.route) {
                            // Clear back stack and navigate to the selected destination
                            navController.navigate(item.route) {
                                // Clear the entire back stack and start fresh
                                popUpTo(0) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}
