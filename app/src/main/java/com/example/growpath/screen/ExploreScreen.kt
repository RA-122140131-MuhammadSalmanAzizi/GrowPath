package com.example.growpath.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.growpath.model.Roadmap
import com.example.growpath.repository.RoadmapRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.catch
import dagger.hilt.android.lifecycle.HiltViewModel // Added for Hilt
import javax.inject.Inject // Added for Hilt
import androidx.hilt.navigation.compose.hiltViewModel // Added for Hilt
import com.example.growpath.component.RoadmapCardEnhanced // Added import for RoadmapCardEnhanced

data class ExploreState(
    val searchQuery: String = "",
    val roadmaps: List<Roadmap> = emptyList(),
    val filteredRoadmaps: List<Roadmap> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel // Added for Hilt
class ExploreViewModel @Inject constructor( // Added @Inject for Hilt
    private val roadmapRepository: RoadmapRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ExploreState())
    val state: StateFlow<ExploreState> = _state.asStateFlow()

    private var roadmapJob: kotlinx.coroutines.Job? = null

    init {
        observeRoadmaps()
    }

    private fun observeRoadmaps() {
        roadmapJob?.cancel()
        roadmapJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            roadmapRepository.getRoadmaps()
                .catch { e ->
                    _state.update {
                        it.copy(
                            error = "Failed to load roadmaps: ${e.message}",
                            isLoading = false
                        )
                    }
                }
                .collect { roadmapList ->
                    val currentQuery = _state.value.searchQuery
                    val newFilteredRoadmaps = if (currentQuery.isBlank()) {
                        roadmapList
                    } else {
                        roadmapList.filter { roadmap ->
                            roadmap.title.contains(currentQuery, ignoreCase = true) ||
                            roadmap.description.contains(currentQuery, ignoreCase = true)
                        }
                    }
                    _state.update {
                        it.copy(
                            roadmaps = roadmapList,
                            filteredRoadmaps = newFilteredRoadmaps,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun onRefresh() {
        observeRoadmaps()
    }

    fun onSearchQueryChange(query: String) {
        _state.update { currentState ->
            val filtered = if (query.isBlank()) {
                currentState.roadmaps
            } else {
                currentState.roadmaps.filter { roadmap ->
                    roadmap.title.contains(query, ignoreCase = true) ||
                    roadmap.description.contains(query, ignoreCase = true)
                }
            }
            currentState.copy(searchQuery = query, filteredRoadmaps = filtered)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onRoadmapClick: (String) -> Unit,
    viewModel: ExploreViewModel = hiltViewModel(),
    dashboardViewModel: DashboardViewModel = hiltViewModel() // Tambahkan akses ke DashboardViewModel
) {
    val state by viewModel.state.collectAsState()
    val dashboardState by dashboardViewModel.state.collectAsState() // Untuk mengakses status favorit

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explore Roadmaps") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = Color.Transparent // Membuat container Scaffold menjadi transparan
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search roadmaps...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else if (state.filteredRoadmaps.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No roadmaps found",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.filteredRoadmaps) { roadmap ->
                        RoadmapCardEnhanced(
                            roadmap = roadmap,
                            isFavorite = dashboardState.favoriteRoadmaps.any { it.id == roadmap.id },
                            onClick = { onRoadmapClick(roadmap.id) },
                            onFavoriteClick = { dashboardViewModel.toggleFavorite(it) }
                        )
                    }
                }
            }
        }
    }
}

