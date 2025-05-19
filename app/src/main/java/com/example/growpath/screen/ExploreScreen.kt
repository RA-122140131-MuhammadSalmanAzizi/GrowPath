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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.growpath.model.Roadmap
import com.example.growpath.repository.RoadmapRepository
import com.example.growpath.utils.ViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExploreState(
    val searchQuery: String = "",
    val roadmaps: List<Roadmap> = emptyList(),
    val filteredRoadmaps: List<Roadmap> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ExploreViewModel(private val roadmapRepository: RoadmapRepository) : ViewModel() {
    private val _state = MutableStateFlow(ExploreState())
    val state: StateFlow<ExploreState> = _state.asStateFlow()

    init {
        loadRoadmaps()
    }

    private fun loadRoadmaps() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val roadmaps = roadmapRepository.getRoadmaps()
                _state.update {
                    it.copy(
                        roadmaps = roadmaps,
                        filteredRoadmaps = roadmaps,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to load roadmaps: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update {
            val filtered = if (query.isBlank()) {
                _state.value.roadmaps
            } else {
                _state.value.roadmaps.filter { roadmap ->
                    roadmap.title.contains(query, ignoreCase = true) ||
                    roadmap.description.contains(query, ignoreCase = true)
                }
            }
            it.copy(searchQuery = query, filteredRoadmaps = filtered)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onRoadmapClick: (String) -> Unit,
    viewModel: ExploreViewModel = viewModel(factory = ViewModelFactory())
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explore Roadmaps") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
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
                            onClick = { onRoadmapClick(roadmap.id) }
                        )
                    }
                }
            }
        }
    }
}
