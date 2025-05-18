package com.example.growpath.presentation.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

object Extensions {
    // String extensions
    fun String.capitalizeWords(): String {
        return this.split(" ").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
    }

    // Toast extensions
    fun Context.showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, length).show()
    }

    // Flow collectors
    @Composable
    fun <T> Flow<T>.collectWithEffect(
        key: Any = Unit,
        action: suspend (T) -> Unit
    ) {
        val context = LocalContext.current
        LaunchedEffect(key) {
            this@collectWithEffect.onEach { action(it) }.collect()
        }
    }

    // Text styling
    fun buildHighlightedText(
        normalText: String,
        highlightedText: String,
        normalStyle: SpanStyle,
        highlightStyle: SpanStyle
    ): AnnotatedString {
        return buildAnnotatedString {
            withStyle(style = normalStyle) {
                append(normalText)
            }
            withStyle(style = highlightStyle) {
                append(highlightedText)
            }
        }
    }

    // FontWeight helpers
    val FontWeight.Companion.SemiBold: FontWeight
        get() = FontWeight(600)
}
