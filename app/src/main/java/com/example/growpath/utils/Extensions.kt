package com.example.growpath.utils

fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.uppercase() } }
