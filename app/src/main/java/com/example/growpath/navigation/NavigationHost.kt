package com.example.growpath.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.growpath.screen.*

@Composable
fun NavigationHost(
    navController: NavHostController,
    startDestination: String = NavGraph.LOGIN,
    modifier: Modifier = Modifier
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0), // Remove default window insets
        bottomBar = {
            // Hanya tampilkan bottom navigation bar jika bukan di halaman login
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (currentRoute != NavGraph.LOGIN) {
                BottomNavigationBar(navController = navController)
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(
                    bottom = innerPadding.calculateBottomPadding(),
                    // Use smaller top padding to reduce top thickness
                    top = 4.dp
                )
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = modifier
            ) {
                // Login route
                composable(NavGraph.LOGIN) {
                    AuthScreen(
                        onLoginSuccess = {
                            navController.navigate(NavGraph.DASHBOARD) {
                                // Hapus login dari back stack agar tidak bisa kembali ke login
                                popUpTo(NavGraph.LOGIN) { inclusive = true }
                            }
                        }
                    )
                }

                composable(NavGraph.DASHBOARD) {
                    DashboardScreen(
                        onRoadmapClick = { roadmapId ->
                            navController.navigate(NavGraph.roadmapWithId(roadmapId))
                        },
                        onProfileClick = {
                            navController.navigate(NavGraph.PROFILE)
                        },
                        navController = navController // Pass NavController
                    )
                }

                composable(NavGraph.PROFILE) {
                    ProfileScreen(
                        onBackClick = { navController.popBackStack() },
                        navController = navController // Pass NavController
                    )
                }

                composable(NavGraph.ACHIEVEMENTS) {
                    AchievementsScreen(
                        // Pass NavController if needed for navigation from AchievementsScreen
                    )
                }

                composable(NavGraph.EXPLORE) {
                    ExploreScreen(
                        onRoadmapClick = { roadmapId ->
                            navController.navigate(NavGraph.roadmapWithId(roadmapId))
                        }
                    )
                }

                composable(
                    route = NavGraph.ROADMAP,
                    arguments = listOf(navArgument("roadmapId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val roadmapId = backStackEntry.arguments?.getString("roadmapId") ?: return@composable
                    RoadmapScreen(
                        roadmapId = roadmapId,
                        onMilestoneClick = { milestoneId ->
                            navController.navigate(NavGraph.milestoneWithId(milestoneId))
                        },
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(
                    route = NavGraph.MILESTONE,
                    arguments = listOf(navArgument("milestoneId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val milestoneId = backStackEntry.arguments?.getString("milestoneId") ?: return@composable
                    MilestoneScreen(
                        milestoneId = milestoneId,
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(NavGraph.POMODORO) {
                    PomodoroScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(NavGraph.NOTIFICATIONS) {
                    NotificationsScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(NavGraph.ABOUT) {
                    AboutScreen()
                }

                composable(NavGraph.POMODORO_TIMER) { // Added Pomodoro Timer screen
                    PomodoroTimerScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
