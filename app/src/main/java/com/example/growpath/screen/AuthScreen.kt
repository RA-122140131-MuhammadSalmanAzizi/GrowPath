package com.example.growpath.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.growpath.R
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    // State for login/register mode
    var isLoginMode by remember { mutableStateOf(true) }

    // Form states
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Observe ViewModel state
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Pre-fill with current username if in login mode
    LaunchedEffect(Unit) {
        if (isLoginMode) {
            viewModel.getCurrentUsername()?.let {
                username = it
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(if (isLoginMode) "Login to GrowPath" else "Register for GrowPath")
            })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFBCEAE7)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App Logo
                Image(
                    painter = painterResource(id = R.drawable.growpath_logo),
                    contentDescription = "GrowPath Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 32.dp)
                )

                // Username & Password Fields
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        viewModel.clearError()
                    },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        viewModel.clearError()
                    },
                    label = { Text("Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Confirm password field (only in register mode)
                if (!isLoginMode) {
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            viewModel.clearError()
                        },
                        label = { Text("Confirm Password") },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isError = password != confirmPassword && confirmPassword.isNotEmpty()
                    )

                    if (password != confirmPassword && confirmPassword.isNotEmpty()) {
                        Text(
                            text = "Passwords do not match",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.align(Alignment.Start)
                        )
                    }
                }

                // Error message
                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Login/Register Button
                Button(
                    onClick = {
                        if (isLoginMode) {
                            // Login action
                            viewModel.login(username, password) {
                                // Login successful callback
                                Log.d("AuthScreen", "Login successful with username: $username")
                                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                                onLoginSuccess()
                            }
                        } else {
                            // Register action - first check passwords match
                            if (password == confirmPassword && password.length >= 4) {
                                viewModel.register(username, password) {
                                    // Register successful callback
                                    Log.d("AuthScreen", "Registration successful with username: $username")
                                    Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess()
                                }
                            } else if (password.length < 4) {
                                // Show error for password too short
                                Toast.makeText(context, "Password must be at least 4 characters", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && username.isNotBlank() && password.isNotBlank() &&
                            (!isLoginMode && password == confirmPassword || isLoginMode)
                ) {
                    Text(if (isLoginMode) "Login" else "Register")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Switch between login and register mode
                TextButton(
                    onClick = {
                        isLoginMode = !isLoginMode
                        // Clear fields when switching modes
                        if (!isLoginMode) {
                            password = ""
                            confirmPassword = ""
                        }
                        viewModel.clearError()
                    }
                ) {
                    Text(
                        text = if (isLoginMode) "New user? Register here" else "Already have an account? Login",
                        textAlign = TextAlign.Center
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
