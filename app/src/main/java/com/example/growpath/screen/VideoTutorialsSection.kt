package com.example.growpath.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Component that displays a list of video tutorials with clickable links
 */
@Composable
fun VideoTutorialsSection(
    title: String = "Video Tutorials",
    videos: List<VideoTutorial> = dummyVideos
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f)
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
                    Icons.Default.VideoLibrary,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            videos.forEach { video ->
                VideoTutorialItem(
                    video = video,
                    onClick = {
                        // Open the video URL in a browser or YouTube app
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.url))
                        context.startActivity(intent)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun VideoTutorialItem(
    video: VideoTutorial,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play video",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${video.duration} • ${video.author}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
 * Data class representing a video tutorial
 */
data class VideoTutorial(
    val title: String,
    val url: String,
    val duration: String,
    val author: String
)

/**
 * List of dummy video tutorials with YouTube links
 */
val dummyVideos = listOf(
    VideoTutorial(
        title = "Getting Started with Android Development",
        url = "https://www.youtube.com/watch?v=fis26HvvDII",
        duration = "12:34",
        author = "Android Developers"
    ),
    VideoTutorial(
        title = "Kotlin Fundamentals for Beginners",
        url = "https://www.youtube.com/watch?v=F9UC9DY-vIU",
        duration = "20:15",
        author = "Kotlin Official"
    ),
    VideoTutorial(
        title = "Introduction to Jetpack Compose",
        url = "https://www.youtube.com/watch?v=cDabx3SjuOY",
        duration = "15:42",
        author = "Android Developers"
    ),
    VideoTutorial(
        title = "Building Material 3 UIs in Compose",
        url = "https://www.youtube.com/watch?v=2uRMdZzJKCk",
        duration = "18:23",
        author = "Google Developers"
    ),
    VideoTutorial(
        title = "Android MVVM Architecture Pattern",
        url = "https://www.youtube.com/watch?v=ijXjCtCXcN4",
        duration = "22:07",
        author = "Android Academy"
    )
)
