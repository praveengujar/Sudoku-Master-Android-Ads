package com.sudokumaster.android.presentation.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sudokumaster.android.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val isGuestMode by authViewModel.isGuestMode.collectAsStateWithLifecycle()
    val isLoading by authViewModel.isLoading.collectAsStateWithLifecycle()
    
    // Tab state
    var selectedTab by remember { mutableStateOf(0) }
    
    // Settings state
    var biometricEnabled by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf("Default") }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2),
                        Color(0xFF6B73FF)
                    )
                )
            )
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Profile",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Tab Navigation
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                listOf("Profile", "Settings", "Offline").forEachIndexed { index, title ->
                    Card(
                        onClick = { selectedTab = index },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedTab == index) 
                                Color(0xFF00D4FF) 
                            else 
                                Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            textAlign = TextAlign.Center,
                            color = if (selectedTab == index) Color.White else Color.White.copy(alpha = 0.7f),
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> ProfileTabContent(
                currentUser = currentUser,
                isGuestMode = isGuestMode,
                onLogout = {
                    scope.launch {
                        authViewModel.logout()
                        onLogout()
                    }
                },
                isLoading = isLoading
            )
            1 -> SettingsTabContent(
                biometricEnabled = biometricEnabled,
                onBiometricToggle = { biometricEnabled = !biometricEnabled },
                selectedTheme = selectedTheme,
                onThemeChange = { selectedTheme = it },
                notificationsEnabled = notificationsEnabled,
                onNotificationsToggle = { notificationsEnabled = !notificationsEnabled },
                soundEnabled = soundEnabled,
                onSoundToggle = { soundEnabled = !soundEnabled },
                vibrationEnabled = vibrationEnabled,
                onVibrationToggle = { vibrationEnabled = !vibrationEnabled }
            )
            2 -> OfflineTabContent()
        }
    }
}

@Composable
private fun ProfileTabContent(
    currentUser: Any?, // Replace with proper User type
    isGuestMode: Boolean,
    onLogout: () -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // User Stats Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Statistics",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B73FF),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(label = "Games Played", value = "0")
                    StatItem(label = "Best Time", value = "--:--")
                    StatItem(label = "Level", value = "Beginner")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Player Level Progress
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Player Level: Beginner",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF667eea)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LinearProgressIndicator(
                        progress = 0.3f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF00D4FF),
                        trackColor = Color(0xFF6B73FF).copy(alpha = 0.2f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "30% to Intermediate",
                        fontSize = 12.sp,
                        color = Color(0xFF667eea).copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Achievements Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Recent Achievements",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B73FF),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                AchievementItem(
                    icon = "🎯",
                    title = "First Steps",
                    description = "Play your first game",
                    isUnlocked = false
                )
                
                AchievementItem(
                    icon = "⚡",
                    title = "Speed Solver",
                    description = "Complete a puzzle in under 5 minutes",
                    isUnlocked = false
                )
                
                AchievementItem(
                    icon = "🧠",
                    title = "No Hints Needed",
                    description = "Complete a puzzle without hints",
                    isUnlocked = false
                )
            }
        }
        
        // Logout Button in Profile Tab
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red.copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isGuestMode) "Exit Guest Mode" else "Logout",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun SettingsTabContent(
    biometricEnabled: Boolean,
    onBiometricToggle: () -> Unit,
    selectedTheme: String,
    onThemeChange: (String) -> Unit,
    notificationsEnabled: Boolean,
    onNotificationsToggle: () -> Unit,
    soundEnabled: Boolean,
    onSoundToggle: () -> Unit,
    vibrationEnabled: Boolean,
    onVibrationToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Game Settings Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Game Preferences",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B73FF),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                SettingToggleItem(
                    title = "Sound Effects",
                    subtitle = "Enable game sound effects",
                    isEnabled = soundEnabled,
                    onToggle = onSoundToggle,
                    icon = Icons.Default.VolumeUp
                )

                SettingToggleItem(
                    title = "Vibration",
                    subtitle = "Haptic feedback for interactions",
                    isEnabled = vibrationEnabled,
                    onToggle = onVibrationToggle,
                    icon = Icons.Default.Vibration
                )

                SettingToggleItem(
                    title = "Notifications",
                    subtitle = "Daily puzzle reminders",
                    isEnabled = notificationsEnabled,
                    onToggle = onNotificationsToggle,
                    icon = Icons.Default.Notifications
                )
            }
        }

        // Security Settings Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Security",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B73FF),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                SettingToggleItem(
                    title = "Biometric Authentication",
                    subtitle = "Use fingerprint or face ID",
                    isEnabled = biometricEnabled,
                    onToggle = onBiometricToggle,
                    icon = Icons.Default.Fingerprint
                )

                Divider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color(0xFF6B73FF).copy(alpha = 0.2f)
                )

                SettingsClickableItem(
                    title = "Change Password",
                    subtitle = "Update your account password",
                    icon = Icons.Default.Lock,
                    onClick = { /* TODO: Implement password change */ }
                )
            }
        }

        // Theme Settings Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Appearance",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B73FF),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val themes = listOf("Default", "Dark", "Blue", "Green")
                themes.forEach { theme ->
                    ThemeSelectionItem(
                        themeName = theme,
                        isSelected = selectedTheme == theme,
                        onSelect = { onThemeChange(theme) }
                    )
                }
            }
        }

        // About Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "About",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B73FF),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                SettingsClickableItem(
                    title = "Version",
                    subtitle = "1.0.0",
                    icon = Icons.Default.Info,
                    onClick = { }
                )

                SettingsClickableItem(
                    title = "Terms of Service",
                    subtitle = "Read our terms and conditions",
                    icon = Icons.Default.Description,
                    onClick = { /* TODO: Open terms */ }
                )

                SettingsClickableItem(
                    title = "Privacy Policy",
                    subtitle = "Learn about data usage",
                    icon = Icons.Default.PrivacyTip,
                    onClick = { /* TODO: Open privacy policy */ }
                )
            }
        }
    }
}

@Composable
private fun OfflineTabContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Offline Mode Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Offline Mode",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B73FF),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Play Sudoku without an internet connection. Download puzzles to play offline.",
                    fontSize = 14.sp,
                    color = Color(0xFF667eea),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enable Offline Mode",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6B73FF)
                    )

                    Switch(
                        checked = false,
                        onCheckedChange = { /* TODO: Toggle offline mode */ },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF00D4FF),
                            checkedTrackColor = Color(0xFF00D4FF).copy(alpha = 0.5f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status info
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = null,
                        tint = Color(0xFF667eea),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Status: Online Mode",
                        fontSize = 14.sp,
                        color = Color(0xFF667eea)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = Color(0xFF667eea),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Downloaded Puzzles: 0",
                        fontSize = 14.sp,
                        color = Color(0xFF667eea)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Download button
                Button(
                    onClick = { /* TODO: Download puzzles */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00D4FF)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Download Puzzles",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Offline Features Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Offline Features",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B73FF),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OfflineFeatureItem(
                    icon = Icons.Default.WifiOff,
                    title = "No Internet Required",
                    description = "Play puzzles without connectivity"
                )

                OfflineFeatureItem(
                    icon = Icons.Default.Download,
                    title = "Pre-downloaded Content",
                    description = "Access library of saved puzzles"
                )

                OfflineFeatureItem(
                    icon = Icons.Default.Save,
                    title = "Local Progress",
                    description = "Game progress saved on device"
                )

                OfflineFeatureItem(
                    icon = Icons.Default.Sync,
                    title = "Auto Sync",
                    description = "Progress syncs when reconnected"
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6B73FF)
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF667eea)
        )
    }
}

@Composable
private fun AchievementItem(
    icon: String,
    title: String,
    description: String,
    isUnlocked: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 24.sp,
            modifier = Modifier.size(32.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (isUnlocked) Color(0xFF6B73FF) else Color(0xFF667eea).copy(alpha = 0.6f)
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = if (isUnlocked) Color(0xFF667eea) else Color(0xFF667eea).copy(alpha = 0.5f)
            )
        }
        
        if (isUnlocked) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Unlocked",
                tint = Color(0xFF00FF88),
                modifier = Modifier.size(20.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = Color(0xFF667eea).copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingToggleItem(
    title: String,
    subtitle: String,
    isEnabled: Boolean,
    onToggle: () -> Unit,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF6B73FF),
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6B73FF)
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color(0xFF667eea)
            )
        }
        
        Switch(
            checked = isEnabled,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF00D4FF),
                checkedTrackColor = Color(0xFF00D4FF).copy(alpha = 0.5f),
                uncheckedThumbColor = Color.White.copy(alpha = 0.6f),
                uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsClickableItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6B73FF).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF6B73FF),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B73FF)
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color(0xFF667eea)
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF667eea),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ThemeSelectionItem(
    themeName: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        onClick = onSelect,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                Color(0xFF6B73FF).copy(alpha = 0.2f) 
            else 
                Color(0xFF6B73FF).copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = themeName,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                color = Color(0xFF6B73FF),
                modifier = Modifier.weight(1f)
            )
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = Color(0xFF00D4FF),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun OfflineFeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF6B73FF),
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6B73FF)
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color(0xFF667eea)
            )
        }
    }
}