package com.example.growpath.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(
    notificationId: String,
    onBackClick: () -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val notification = state.notifications.find { it.id == notificationId }
    val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())

    // Mark notification as read when opened
    LaunchedEffect(notificationId) {
        viewModel.markAsRead(notificationId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(notification?.title ?: "Notification Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    // Mengubah warna header menjadi transparan untuk gradasi
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                modifier = Modifier.background(
                    // Menambahkan gradient seperti halaman notifikasi
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF66D2CC),
                            Color(0xFF6CB8B7)
                        )
                    )
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        if (notification == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = "Notification not found",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            // Tampilan detail notifikasi yang lebih minimalis dan rapi
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Judul notifikasi
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tanggal notifikasi
                Text(
                    text = "Diterima pada: ${dateFormat.format(notification.timestamp)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Pesan ringkas
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Garis pembatas jika ada konten detail
                if (notification.detailedContent.isNotBlank()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(thickness = 0.5.dp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Detail Informasi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, end = 4.dp)
                    ) {
                        Text(
                            text = "Terima kasih atas partisipasi Anda dalam aplikasi GrowPath. Berikut adalah beberapa informasi yang mungkin berguna untuk Anda:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Poin-poin penting
                        PointItem(
                            title = "Kemajuan Pembelajaran",
                            description = "Anda telah menyelesaikan 60% dari roadmap yang dipilih. Lanjutkan untuk mendapatkan pencapaian berikutnya."
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        PointItem(
                            title = "Pencapaian Baru",
                            description = "Selamat! Anda telah membuka pencapaian \"Learner Enthusiast\" dengan menyelesaikan 5 milestone berturut-turut."
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        PointItem(
                            title = "Rekomendasi",
                            description = "Berdasarkan kemajuan Anda, kami merekomendasikan untuk menjelajahi roadmap \"Advanced Mobile Development\" untuk meningkatkan keterampilan Anda."
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Kami selalu berusaha meningkatkan pengalaman belajar Anda di GrowPath. Jangan ragu untuk memberikan masukan atau saran untuk perbaikan aplikasi di masa mendatang.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Tim GrowPath",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun PointItem(title: String, description: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray
        )
    }
}
