package com.sudokumaster.android.presentation.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Text(
                    text = "Sudoku Master",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = if (isRegistering) "Create Account" else "Welcome Back",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
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
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true
                )

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
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
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = enableBiometric,
                            onCheckedChange = { enableBiometric = it },
                            enabled = !isLoading
                        )
                    }
                }

                // Main action button
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
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && username.isNotBlank() && password.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(if (isRegistering) "Create Account" else "Sign In")
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
                        else "Don't have an account? Create one"
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
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Continue as Guest (Offline Mode)")
                }

                // Debug info (only in debug builds)
                if (hasStoredCredentials) {
                    Text(
                        text = "• Saved login available",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}