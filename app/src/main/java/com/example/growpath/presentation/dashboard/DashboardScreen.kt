package com.example.growpath.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.growpath.data.model.Roadmap
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onRoadmapClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = state.isLoading)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GrowPath") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::onCreateRoadmapClick) {
                Icon(Icons.Default.Add, contentDescription = "Create new roadmap")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = viewModel::onRefresh
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    UserProgressCard(
                        userName = state.userName,
                        level = state.userLevel,
                        experience = state.userExperience
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (state.inProgressRoadmaps.isNotEmpty()) {
                    item {
                        Text(
                            text = "In Progress",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    items(state.inProgressRoadmaps) { roadmap ->
                        RoadmapCard(
                            roadmap = roadmap,
                            onClick = { viewModel.onRoadmapClick(roadmap.id) }
                        )
                    }
                }

                if (state.notStartedRoadmaps.isNotEmpty()) {
                    item {
                        Text(
                            text = "Not Started",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    items(state.notStartedRoadmaps) { roadmap ->
                        RoadmapCard(
                            roadmap = roadmap,
                            onClick = { viewModel.onRoadmapClick(roadmap.id) }
                        )
                    }
                }

                if (state.completedRoadmaps.isNotEmpty()) {
                    item {
                        Text(
                            text = "Completed",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    items(state.completedRoadmaps) { roadmap ->
                        RoadmapCard(
                            roadmap = roadmap,
                            onClick = { viewModel.onRoadmapClick(roadmap.id) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp)) // For FAB
                }
            }
        }
    }
}

@Composable
fun UserProgressCard(
    userName: String,
    level: Int,
    experience: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Welcome back, $userName",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Level $level",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = (experience % 100) / 100f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$experience XP",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadmapCard(
    roadmap: Roadmap,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = roadmap.title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = roadmap.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = roadmap.progress,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${(roadmap.progress * 100).toInt()}% Complete",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
