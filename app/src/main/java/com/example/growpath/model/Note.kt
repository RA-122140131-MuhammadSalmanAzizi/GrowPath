package com.example.growpath.model

data class Note(
    val id: String,
    val milestoneId: String,
    val content: String,
    val createdAt: Long
)
