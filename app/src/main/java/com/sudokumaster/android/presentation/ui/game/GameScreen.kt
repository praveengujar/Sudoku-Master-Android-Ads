package com.sudokumaster.android.presentation.ui.game

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sudokumaster.android.domain.model.SudokuDifficulty
import com.sudokumaster.android.presentation.ui.game.components.SudokuBoard
import com.sudokumaster.android.presentation.ui.game.components.NumberPad
import com.sudokumaster.android.presentation.ui.game.components.VictoryDialog
import com.sudokumaster.android.presentation.viewmodel.AuthViewModel
import com.sudokumaster.android.presentation.viewmodel.SudokuGameViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    authViewModel: AuthViewModel,
    onNavigateToAuth: () -> Unit,
    sudokuViewModel: SudokuGameViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    
    // Auth state
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val isGuestMode by authViewModel.isGuestMode.collectAsStateWithLifecycle()
    
    // Game state
    val grid by sudokuViewModel.grid.collectAsStateWithLifecycle()
    val originalGrid by sudokuViewModel.originalGrid.collectAsStateWithLifecycle()
    val selectedCell by sudokuViewModel.selectedCell.collectAsStateWithLifecycle()
    val difficulty by sudokuViewModel.difficulty.collectAsStateWithLifecycle()
    val errors by sudokuViewModel.errors.collectAsStateWithLifecycle()
    val isLoading by sudokuViewModel.isLoading.collectAsStateWithLifecycle()
    val isVictory by sudokuViewModel.isVictory.collectAsStateWithLifecycle()
    val showVictoryAlert by sudokuViewModel.showVictoryAlert.collectAsStateWithLifecycle()
    val errorMessage by sudokuViewModel.errorMessage.collectAsStateWithLifecycle()
    val timeSpentSeconds by sudokuViewModel.timeSpentSeconds.collectAsStateWithLifecycle()
    val isOfflineMode by sudokuViewModel.isOfflineMode.collectAsStateWithLifecycle()
    val hintCell by sudokuViewModel.hintCell.collectAsStateWithLifecycle()
    
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                )
            )
    ) {
        // Modern top app bar
        CenterAlignedTopAppBar(
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isGuestMode) "Guest Mode" else "Welcome back!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    currentUser?.let { user ->
                        if (!isGuestMode) {
                            Text(
                                text = user.username,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    if (isOfflineMode) {
                        Badge(
                            modifier = Modifier.padding(top = 2.dp),
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Text(
                                text = "Offline",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            },
            actions = {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Profile") },
                        onClick = { 
                            showMenu = false 
                            // TODO: Navigate to profile
                        },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Settings") },
                        onClick = { 
                            showMenu = false 
                            // TODO: Navigate to settings
                        },
                        leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Sign Out") },
                        onClick = { 
                            showMenu = false
                            scope.launch {
                                authViewModel.logout()
                                onNavigateToAuth()
                            }
                        },
                        leadingIcon = { Icon(Icons.AutoMirrored.Default.ExitToApp, contentDescription = null) }
                    )
                }
            }
        )

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Enhanced game info card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Difficulty selector with modern design
                    Column {
                        Text(
                            text = "Difficulty",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        var expanded by remember { mutableStateOf(false) }
                        
                        AssistChip(
                            onClick = { expanded = true },
                            label = {
                                Text(
                                    text = difficulty.displayName,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = difficulty.color.copy(alpha = 0.2f),
                                labelColor = difficulty.color
                            )
                        )
                        
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            SudokuDifficulty.values().forEach { diff ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Circle,
                                                contentDescription = null,
                                                modifier = Modifier.size(12.dp),
                                                tint = diff.color
                                            )
                                            Text(diff.displayName)
                                        }
                                    },
                                    onClick = {
                                        expanded = false
                                        sudokuViewModel.setDifficulty(diff)
                                    }
                                )
                            }
                        }
                    }

                    // Enhanced timer display
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Time",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        ) {
                            Text(
                                text = formatTime(timeSpentSeconds),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Error message with modern styling
            errorMessage?.let { error ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Loading indicator with modern design
            if (isLoading) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            strokeWidth = 4.dp
                        )
                        Text(
                            text = "Loading puzzle...",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    }
                }
            } else {
                // Modern Sudoku board
                SudokuBoard(
                    grid = grid,
                    originalGrid = originalGrid,
                    selectedCell = selectedCell,
                    errors = errors,
                    hintCell = hintCell,
                    onCellClick = { row, col ->
                        sudokuViewModel.setSelectedCell(
                            if (selectedCell?.row == row && selectedCell?.col == col) 
                                null else com.sudokumaster.android.domain.model.CellPosition(row, col)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Enhanced Number pad
                NumberPad(
                    onNumberClick = { number ->
                        sudokuViewModel.enterNumber(number)
                    },
                    onEraseClick = {
                        sudokuViewModel.eraseNumber()
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Modern action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilledTonalButton(
                        onClick = { sudokuViewModel.newGame() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("New Game", fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = { sudokuViewModel.getHint() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Hint", fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = { sudokuViewModel.clearUserInputs() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Clear", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // Victory dialog
    if (showVictoryAlert) {
        VictoryDialog(
            timeSpent = timeSpentSeconds,
            difficulty = difficulty,
            onDismiss = { sudokuViewModel.closeVictoryModal() },
            onNewGame = { 
                sudokuViewModel.closeVictoryModal()
                sudokuViewModel.newGame()
            }
        )
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", minutes, secs)
}