package com.example.growpath.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // State for UI
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Screen state
    var currentScreen by remember { mutableStateOf<AccountScreen>(AccountScreen.Main) }

    // Username change state
    var currentUsername by remember { mutableStateOf("") }
    var newUsername by remember { mutableStateOf("") }
    var passwordForUsernameChange by remember { mutableStateOf("") }
    var isUsernamePasswordVisible by remember { mutableStateOf(false) }

    // Password change state
    var usernameForPasswordChange by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var isCurrentPasswordVisible by remember { mutableStateOf(false) }
    var isNewPasswordVisible by remember { mutableStateOf(false) }

    // Local validation errors
    var usernameChangeError by remember { mutableStateOf<String?>(null) }
    var passwordChangeError by remember { mutableStateOf<String?>(null) }

    // Get the current username
    LaunchedEffect(Unit) {
        viewModel.getCurrentUsername()?.let {
            currentUsername = it
            usernameForPasswordChange = it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (currentScreen) {
                            AccountScreen.Main -> "Account Settings"
                            AccountScreen.ChangeUsername -> "Change Username"
                            AccountScreen.ChangePassword -> "Change Password"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (currentScreen != AccountScreen.Main) {
                                currentScreen = AccountScreen.Main
                                // Reset error messages when returning to main menu
                                usernameChangeError = null
                                passwordChangeError = null
                                viewModel.clearError()
                            } else {
                                onNavigateBack()
                            }
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                AccountScreen.Main -> {
                    AccountMainMenu(
                        onChangeUsernameClick = { currentScreen = AccountScreen.ChangeUsername },
                        onChangePasswordClick = { currentScreen = AccountScreen.ChangePassword }
                    )
                }
                AccountScreen.ChangeUsername -> {
                    ChangeUsernameScreen(
                        currentUsername = currentUsername,
                        onCurrentUsernameChange = { currentUsername = it },
                        newUsername = newUsername,
                        onNewUsernameChange = {
                            newUsername = it
                            usernameChangeError = null
                        },
                        password = passwordForUsernameChange,
                        onPasswordChange = {
                            passwordForUsernameChange = it
                            usernameChangeError = null
                        },
                        isPasswordVisible = isUsernamePasswordVisible,
                        onPasswordVisibilityChange = { isUsernamePasswordVisible = it },
                        error = usernameChangeError ?: errorMessage,
                        isLoading = isLoading,
                        onUpdateClick = {
                            if (newUsername.isEmpty()) {
                                usernameChangeError = "New username cannot be empty"
                                return@ChangeUsernameScreen
                            }

                            if (passwordForUsernameChange.isEmpty()) {
                                usernameChangeError = "Password is required to confirm change"
                                return@ChangeUsernameScreen
                            }

                            viewModel.changeUsername(
                                oldUsername = currentUsername,
                                password = passwordForUsernameChange,
                                newUsername = newUsername
                            ) {
                                // Success callback
                                Toast.makeText(context, "Username changed successfully", Toast.LENGTH_SHORT).show()
                                currentUsername = newUsername
                                newUsername = ""
                                passwordForUsernameChange = ""
                                currentScreen = AccountScreen.Main
                            }
                        }
                    )
                }
                AccountScreen.ChangePassword -> {
                    ChangePasswordScreen(
                        username = usernameForPasswordChange,
                        onUsernameChange = { usernameForPasswordChange = it },
                        currentPassword = currentPassword,
                        onCurrentPasswordChange = {
                            currentPassword = it
                            passwordChangeError = null
                        },
                        newPassword = newPassword,
                        onNewPasswordChange = {
                            newPassword = it
                            passwordChangeError = null
                        },
                        confirmNewPassword = confirmNewPassword,
                        onConfirmNewPasswordChange = {
                            confirmNewPassword = it
                            passwordChangeError = null
                        },
                        isCurrentPasswordVisible = isCurrentPasswordVisible,
                        onCurrentPasswordVisibilityChange = { isCurrentPasswordVisible = it },
                        isNewPasswordVisible = isNewPasswordVisible,
                        onNewPasswordVisibilityChange = { isNewPasswordVisible = it },
                        error = passwordChangeError ?: errorMessage,
                        isLoading = isLoading,
                        onUpdateClick = {
                            if (currentPassword.isEmpty()) {
                                passwordChangeError = "Current password is required"
                                return@ChangePasswordScreen
                            }

                            if (newPassword.isEmpty()) {
                                passwordChangeError = "New password cannot be empty"
                                return@ChangePasswordScreen
                            }

                            if (newPassword != confirmNewPassword) {
                                passwordChangeError = "New passwords do not match"
                                return@ChangePasswordScreen
                            }

                            viewModel.changePassword(
                                username = usernameForPasswordChange,
                                oldPassword = currentPassword,
                                newPassword = newPassword
                            ) {
                                // Success callback
                                Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show()
                                currentPassword = ""
                                newPassword = ""
                                confirmNewPassword = ""
                                currentScreen = AccountScreen.Main
                            }
                        }
                    )
                }
            }

            // Loading indicator
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun AccountMainMenu(
    onChangeUsernameClick: () -> Unit,
    onChangePasswordClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Account Security Settings",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                AccountMenuOption(
                    icon = Icons.Default.Person,
                    title = "Change Username",
                    description = "Update your account username",
                    onClick = onChangeUsernameClick
                )

                Divider(modifier = Modifier.padding(horizontal = 16.dp))

                AccountMenuOption(
                    icon = Icons.Default.Lock,
                    title = "Change Password",
                    description = "Update your account password",
                    onClick = onChangePasswordClick
                )
            }
        }
    }
}

@Composable
fun AccountMenuOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ChangeUsernameScreen(
    currentUsername: String,
    onCurrentUsernameChange: (String) -> Unit,
    newUsername: String,
    onNewUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    error: String?,
    isLoading: Boolean,
    onUpdateClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Update Your Username",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Ganti OutlinedTextField dengan field yang read-only
        OutlinedTextField(
            value = currentUsername,
            onValueChange = { /* No-op, field is read-only */ },
            label = { Text("Current Username") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false, // Field dinonaktifkan sehingga tidak dapat diedit
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = LocalContentColor.current.copy(alpha = 0.8f), // Tetap terlihat jelas meski dinonaktifkan
                disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newUsername,
            onValueChange = onNewUsernameChange,
            label = { Text("New Username") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Confirm Password") },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { onPasswordVisibilityChange(!isPasswordVisible) }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        // Error message
        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onUpdateClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && currentUsername.isNotBlank() && newUsername.isNotBlank() && password.isNotBlank()
        ) {
            Text("Update Username")
        }
    }
}

@Composable
fun ChangePasswordScreen(
    username: String,
    onUsernameChange: (String) -> Unit,
    currentPassword: String,
    onCurrentPasswordChange: (String) -> Unit,
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmNewPassword: String,
    onConfirmNewPasswordChange: (String) -> Unit,
    isCurrentPasswordVisible: Boolean,
    onCurrentPasswordVisibilityChange: (Boolean) -> Unit,
    isNewPasswordVisible: Boolean,
    onNewPasswordVisibilityChange: (Boolean) -> Unit,
    error: String?,
    isLoading: Boolean,
    onUpdateClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Update Your Password",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Ganti OutlinedTextField dengan field yang read-only
        OutlinedTextField(
            value = username,
            onValueChange = { /* No-op, field is read-only */ },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false, // Field dinonaktifkan sehingga tidak dapat diedit
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = LocalContentColor.current.copy(alpha = 0.8f), // Tetap terlihat jelas meski dinonaktifkan
                disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = currentPassword,
            onValueChange = onCurrentPasswordChange,
            label = { Text("Current Password") },
            visualTransformation = if (isCurrentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { onCurrentPasswordVisibilityChange(!isCurrentPasswordVisible) }) {
                    Icon(
                        imageVector = if (isCurrentPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (isCurrentPasswordVisible) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            label = { Text("New Password") },
            visualTransformation = if (isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { onNewPasswordVisibilityChange(!isNewPasswordVisible) }) {
                    Icon(
                        imageVector = if (isNewPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (isNewPasswordVisible) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmNewPassword,
            onValueChange = onConfirmNewPasswordChange,
            label = { Text("Confirm New Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        // Error message
        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onUpdateClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading &&
                    username.isNotBlank() &&
                    currentPassword.isNotBlank() &&
                    newPassword.isNotBlank() &&
                    confirmNewPassword.isNotBlank()
        ) {
            Text("Update Password")
        }
    }
}

// Screen states for the UI
enum class AccountScreen {
    Main,
    ChangeUsername,
    ChangePassword
}
