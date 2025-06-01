package com.example.growpath.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.growpath.model.Achievement
import com.example.growpath.navigation.NavGraph
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    navController: NavController? = null,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editedDisplayName by remember { mutableStateOf("") }

    LaunchedEffect(state.user) {
        state.user?.displayName?.let { 
            editedDisplayName = it 
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    IconButton(onClick = { showEditProfileDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent // Membuat container Scaffold menjadi transparan
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Loading indicator
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile header with gradient background
                    ProfileHeader(user = state.user)

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Stats section
                    StatsSection(user = state.user)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Recent achievements section
                    RecentAchievements(
                        achievements = state.achievements.filter { it.isUnlocked }.take(3),
                        onViewAllClick = { navController?.navigate(NavGraph.ACHIEVEMENTS) }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Settings/options - Pass navController and viewModel
                    SettingsSection(navController = navController, viewModel = viewModel)
                }
            }
        }
    }
    
    // Edit Profile Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column {
                    val maxLength = 20

                    OutlinedTextField(
                        value = editedDisplayName,
                        onValueChange = {
                            // Batasi input hingga maksimal 20 karakter
                            if (it.length <= maxLength) editedDisplayName = it
                        },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = {
                            Text(
                                text = "${editedDisplayName.length}/$maxLength",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                            )
                        },
                        isError = editedDisplayName.length > maxLength
                    )
                }
            },
            containerColor = Color.White, // Mengubah warna dialog menjadi putih
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateProfile(editedDisplayName)
                        showEditProfileDialog = false
                    },
                    enabled = editedDisplayName.isNotBlank() && editedDisplayName.length <= 20
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileHeader(user: com.example.growpath.model.User?) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF66D2CC),
            Color(0xFF6CB8B7)
        )
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(brush = gradientBrush)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile picture
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Jika ada photoUrl, tampilkan dengan Coil
                if (user?.photoUrl != null) {
                    androidx.compose.foundation.Image(
                        painter = coil.compose.rememberAsyncImagePainter(
                            model = user.photoUrl,
                            error = coil.compose.rememberAsyncImagePainter(
                                model = androidx.compose.ui.res.painterResource(
                                    id = android.R.drawable.ic_menu_gallery
                                )
                            )
                        ),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    // Default profile icon if no photo
                    Surface(
                        modifier = Modifier.size(120.dp),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .padding(24.dp)
                                .size(72.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // User name
            Text(
                text = user?.displayName ?: "Unknown User",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
            
            // Tidak perlu menampilkan email, karena sudah ada username di atas

            Spacer(modifier = Modifier.height(8.dp))

            // Joined date - menggunakan tanggal saat ini agar dinamis
            val joinDate = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())

            Text(
                text = "Member since $joinDate",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun StatsSection(user: com.example.growpath.model.User?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White) // Mengubah menjadi warna putih
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Your Stats",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Default.Timeline,
                    label = "Experience",
                    value = "${user?.experience ?: 0} XP"
                )
                
                StatItem(
                    icon = Icons.Default.Star,
                    label = "Level",
                    value = "${user?.level ?: 1}"
                )
                
                StatItem(
                    icon = Icons.Default.CheckCircle,
                    label = "Next Level",
                    value = "${user?.let { it.level + 1 } ?: 2}"
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress to next level
            Column {
                // Gunakan konstanta XP per level yang sama dengan yang digunakan di aplikasi (1000)
                val xpPerLevel = 1000
                val currentLevelXp = user?.experience?.rem(xpPerLevel) ?: 0
                val xpToNextLevel = xpPerLevel - currentLevelXp
                val progress = currentLevelXp.toFloat() / xpPerLevel.toFloat()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progress to Level ${user?.let { it.level + 1 } ?: 2}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "$currentLevelXp/$xpPerLevel XP",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    trackColor = Color.Gray.copy(alpha = 0.2f) // Mengubah track menjadi abu-abu transparan
                )
            }
        }
    }
}

@Composable
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun RecentAchievements(
    achievements: List<Achievement>,
    onViewAllClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White) // Mengubah menjadi warna putih
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Achievements",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = onViewAllClick) {
                    Text("View All")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (achievements.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No achievements unlocked yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(achievements) { achievement ->
                        AchievementItem(achievement = achievement)
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementItem(achievement: Achievement) {
    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    
    Card(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // Mengubah warna menjadi putih
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Achievement icon
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            achievement.unlockedAt?.let { timestamp ->
                Text(
                    text = "Unlocked: ${dateFormat.format(Date(timestamp))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    navController: NavController? = null,
    viewModel: ProfileViewModel? = null
) {
    var showAboutDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tambahkan tombol Account Settings
            SettingsItem(
                icon = Icons.Default.Security,
                title = "Account Settings",
                subtitle = "Change username and password",
                onClick = {
                    navController?.navigate(NavGraph.ACCOUNT_SETTINGS)
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            SettingsItem(
                icon = Icons.Default.Info,
                title = "About",
                subtitle = "Learn more about GrowPath",
                onClick = { showAboutDialog = true }
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            SettingsItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                title = "Logout",
                subtitle = "Sign out from your account",
                onClick = { showLogoutDialog = true }
            )
        }
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About GrowPath") },
            icon = { Icon(Icons.Default.Info, contentDescription = null) },
            text = {
                Column {
                    Text(
                        "GrowPath v1.0.0",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "GrowPath adalah aplikasi yang dirancang untuk membantu Anda melacak kemajuan pembelajaran dan pengembangan keterampilan Anda melalui roadmap yang terstruktur.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "© 2025 GrowPath Team",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            containerColor = Color.White, // Mengubah warna dialog menjadi putih
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Apakah Anda yakin ingin keluar dari akun?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        // Perform logout logic
                        viewModel?.logout()
                        // Navigate to login screen
                        navController?.navigate(NavGraph.LOGIN) {
                            // Clear all back stack so user can't go back to protected screens
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text("Logout", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}
