package com.example.growpath.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MilestoneScreen(
    milestoneId: String,
    onBackClick: () -> Unit,
    viewModel: MilestoneViewModel = hiltViewModel()
) {
    LaunchedEffect(milestoneId) {
        viewModel.loadMilestone(milestoneId)
    }

    val state by viewModel.state.collectAsState()
    val milestone = state.milestone
    val notes = state.notes
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showNoteDialog by remember { mutableStateOf(false) }
    var currentNoteContent by remember { mutableStateOf("") }
    var selectedNoteId by remember { mutableStateOf<String?>(null) }

    // Show completion animation when state indicates
    if (state.showCompletionAnimation) {
        CompletionAnimation(
            title = "${milestone?.title ?: "Milestone"} Completed!",
            onAnimationComplete = {
                viewModel.dismissCompletionAnimation()
            }
        )
    }

    // Handle note dialog
    if (showNoteDialog) {
        NoteDialog(
            initialContent = currentNoteContent,
            onSave = { content ->
                if (content.isNotBlank()) {
                    if (selectedNoteId != null) {
                        // Update existing note
                        viewModel.updateExistingNote(selectedNoteId!!, content)
                    } else {
                        // Create new note
                        milestone?.id?.let { milestoneId ->
                            viewModel.updateMilestoneNote(milestoneId, content)
                        }
                    }
                }
                showNoteDialog = false
                currentNoteContent = ""
                selectedNoteId = null
            },
            onDismiss = {
                showNoteDialog = false
                currentNoteContent = ""
                selectedNoteId = null
            },
            onDelete = selectedNoteId?.let { noteId ->
                {
                    viewModel.deleteNote(noteId)
                    showNoteDialog = false
                    currentNoteContent = ""
                    selectedNoteId = null
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(milestone?.title ?: "Milestone Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (milestone != null) {
                FloatingActionButton(
                    onClick = {
                        currentNoteContent = ""
                        selectedNoteId = null
                        showNoteDialog = true
                    },
                    containerColor = Color(0xFF4CAF50) // Hijau utama
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Add Note",
                        tint = Color.White
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Loading state
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Error state
            state.error?.let {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error: $it",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadMilestone(milestoneId) }) {
                        Text("Retry")
                    }
                }
            }

            // Content when milestone is loaded
            AnimatedVisibility(
                visible = !state.isLoading && state.error == null && milestone != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                ) {
                    // Status Card
                    MilestoneStatusCard(
                        isCompleted = milestone?.isCompleted ?: false,
                        onStatusChange = { isCompleted ->
                            milestone?.id?.let {
                                viewModel.toggleMilestoneCompletion(it, isCompleted)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8F5E9) // Light green background
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50) // Sesuaikan dengan warna hijau utama
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Description",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32) // Dark green text
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = milestone?.description ?: "",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Notes Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFC8E6C9) // Light green background
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            // Header stays fixed
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Color(0xFF388E3C) // Medium-dark green
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Journal Notes",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32) // Dark green text
                                )
                            }

                            Divider(color = Color(0xFF81C784).copy(alpha = 0.5f), thickness = 1.dp)

                            // Content area with scrolling
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp) // Fixed height with scrolling
                            ) {
                                if (notes.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No notes yet. Add your first note by clicking the pencil button.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    // Scrollable notes content
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp)
                                            .padding(bottom = 16.dp)
                                    ) {
                                        items(notes) { note ->
                                            NoteCard(
                                                note = note,
                                                onEdit = {
                                                    currentNoteContent = note.content
                                                    selectedNoteId = note.id
                                                    showNoteDialog = true
                                                },
                                                onDelete = {
                                                    viewModel.deleteNote(note.id)
                                                }
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Other Resources Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFB2DFDB) // Light teal-green background
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Resources & Tips",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00695C) // Dark teal-green
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // This is just mock data, you could store real resources in the Milestone model
                            ListItem(
                                headlineContent = { Text("Online Documentation") },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        tint = Color(0xFF00897B) // Medium teal-green
                                    )
                                }
                            )

                            ListItem(
                                headlineContent = { Text("Video Tutorials") },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = Color(0xFF00897B) // Medium teal-green
                                    )
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                "Complete this milestone step by step to gain a solid understanding of the concept.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF004D40).copy(alpha = 0.8f) // Very dark teal-green
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Video Tutorials Section
                    VideoTutorialsSection()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Documentation Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFDCEDC8) // Very light green background
                        )
                    ) {
                        // ...existing code...
                    }

                    Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
                }
            }
        }
    }
}

@Composable
fun NoteCard(
    note: com.example.growpath.model.Note,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete this note? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            val formattedDate = dateFormat.format(Date(note.createdAt))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Note",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { showDeleteConfirmation = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Note",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MilestoneStatusCard(
    isCompleted: Boolean,
    onStatusChange: (Boolean) -> Unit
) {
    val gradientBrush = Brush.linearGradient(
        colors = if (isCompleted) {
            listOf(
                Color(0xFF4CAF50),  // Hijau utama
                Color(0xFF8BC34A)   // Hijau muda
            )
        } else {
            listOf(
                Color(0xFF4CAF50).copy(alpha = 0.8f),  // Hijau utama (lebih transparan)
                Color(0xFF81C784).copy(alpha = 0.8f)   // Hijau medium (lebih transparan)
            )
        }
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(brush = gradientBrush)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .padding(12.dp)
                                .size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Milestone Status",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (isCompleted) "Completed" else "In Progress",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }

                    Switch(
                        checked = isCompleted,
                        onCheckedChange = { newValue ->
                            // Only allow changing from incomplete to complete, not the reverse
                            if (newValue && !isCompleted) {
                                onStatusChange(true)
                            }
                            // Ignore attempts to uncheck the switch (prevents toggling back to incomplete)
                        },
                        enabled = !isCompleted, // Disable the switch once completed
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                            uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            uncheckedTrackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                            // Add darker colors for disabled state
                            disabledCheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            disabledCheckedTrackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isCompleted)
                        "Well done! You've completed this milestone. Completion cannot be undone."
                    else
                        "Toggle the switch when you've completed this milestone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }
    }
}
