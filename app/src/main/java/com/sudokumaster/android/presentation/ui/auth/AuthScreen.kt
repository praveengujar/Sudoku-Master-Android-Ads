package com.sudokumaster.android.presentation.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sudokumaster.android.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    onNavigateToGame: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Collect state
    val isAuthenticated by authViewModel.isAuthenticated.collectAsStateWithLifecycle()
    val isGuestMode by authViewModel.isGuestMode.collectAsStateWithLifecycle()
    val isLoading by authViewModel.isLoading.collectAsStateWithLifecycle()
    val error by authViewModel.error.collectAsStateWithLifecycle()
    val biometricEnabled by authViewModel.biometricEnabled.collectAsStateWithLifecycle()
    val hasStoredCredentials = authViewModel.hasStoredCredentials
    val isBiometricAvailable = authViewModel.isBiometricAvailable
    val biometricDisplayName = authViewModel.biometricDisplayName

    // Local state
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }
    var enableBiometric by remember { mutableStateOf(false) }

    // Navigate to game if authenticated or in guest mode
    LaunchedEffect(isAuthenticated, isGuestMode) {
        if (isAuthenticated || isGuestMode) {
            onNavigateToGame()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1421),
                        Color(0xFF1B2635),
                        Color(0xFF0F1419)
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E2A38).copy(alpha = 0.95f)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Title with vibrant color
                Text(
                    text = "Sudoku Master",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF00D4FF)
                )
                
                Text(
                    text = if (isRegistering) "Create Account" else "Welcome Back",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Error message
                error?.let { errorMessage ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = errorMessage,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Biometric login button (if available and has stored credentials)
                if (isBiometricAvailable && hasStoredCredentials && biometricEnabled && !isRegistering) {
                    Button(
                        onClick = {
                            scope.launch {
                                authViewModel.loginWithBiometric(context as FragmentActivity)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Text("Sign in with $biometricDisplayName")
                    }
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // Username field
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00D4FF),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF00D4FF)
                    )
                )

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00D4FF),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF00D4FF)
                    )
                )

                // Enable biometric switch (for new logins)
                if (isBiometricAvailable) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Enable $biometricDisplayName",
                            modifier = Modifier.weight(1f),
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Switch(
                            checked = enableBiometric,
                            onCheckedChange = { enableBiometric = it },
                            enabled = !isLoading,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF00D4FF),
                                checkedTrackColor = Color(0xFF00D4FF).copy(alpha = 0.5f),
                                uncheckedThumbColor = Color.White.copy(alpha = 0.6f),
                                uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                            )
                        )
                    }
                }

                // Main action button with gradient
                Button(
                    onClick = {
                        scope.launch {
                            authViewModel.clearError()
                            if (isRegistering) {
                                authViewModel.register(username, password)
                            } else {
                                authViewModel.login(username, password, enableBiometric)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF00D4FF),
                                    Color(0xFF0099CC),
                                    Color(0xFF0066FF)
                                )
                            )
                        ),
                    enabled = !isLoading && username.isNotBlank() && password.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = if (isRegistering) "Create Account" else "Sign In",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                // Switch between login/register
                TextButton(
                    onClick = { 
                        isRegistering = !isRegistering
                        authViewModel.clearError()
                    },
                    enabled = !isLoading
                ) {
                    Text(
                        if (isRegistering) "Already have an account? Sign in" 
                        else "Don't have an account? Create one",
                        color = Color(0xFF00D4FF)
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Guest mode button
                OutlinedButton(
                    onClick = {
                        authViewModel.continueAsGuest()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(2.dp, Color(0xFF00FF88)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF00FF88)
                    )
                ) {
                    Text(
                        text = "Continue as Guest (Offline Mode)",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }

                // Debug info (only in debug builds)
                if (hasStoredCredentials) {
                    Text(
                        text = "• Saved login available",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}