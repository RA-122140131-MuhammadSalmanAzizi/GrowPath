package com.example.growpath.presentation.milestone

import com.example.growpath.data.model.Milestone
import com.example.growpath.data.model.Note

data class MilestoneState(
    val milestone: Milestone? = null,
    val notes: List<Note> = emptyList(),
    val newNoteContent: String = "",
    val isNoteDialogVisible: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)
