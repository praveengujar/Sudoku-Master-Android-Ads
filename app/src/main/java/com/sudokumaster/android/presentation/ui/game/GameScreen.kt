package com.sudokumaster.android.presentation.ui.game

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            .padding(16.dp)
    ) {
        // Top bar with user info and menu
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = if (isGuestMode) "Guest Mode" else "Welcome, ${currentUser?.username ?: "Player"}",
                        fontWeight = FontWeight.Medium
                    )
                    if (isOfflineMode) {
                        Text(
                            text = "Offline Mode",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
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
                        leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = null) }
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Game info row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Difficulty selector
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Difficulty: ", fontWeight = FontWeight.Medium)
                
                var expanded by remember { mutableStateOf(false) }
                
                TextButton(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = difficulty.color
                    )
                ) {
                    Text(difficulty.displayName, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
                
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    SudokuDifficulty.values().forEach { diff ->
                        DropdownMenuItem(
                            text = { Text(diff.displayName) },
                            onClick = {
                                expanded = false
                                sudokuViewModel.setDifficulty(diff)
                            }
                        )
                    }
                }
            }

            // Timer
            Text(
                text = formatTime(timeSpentSeconds),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error message
        errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator()
                    Text("Loading puzzle...")
                }
            }
        } else {
            // Sudoku board
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

            Spacer(modifier = Modifier.height(16.dp))

            // Number pad
            NumberPad(
                onNumberClick = { number ->
                    sudokuViewModel.enterNumber(number)
                },
                onEraseClick = {
                    sudokuViewModel.eraseNumber()
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = { sudokuViewModel.newGame() }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Game")
                }

                OutlinedButton(
                    onClick = { sudokuViewModel.getHint() }
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hint")
                }

                OutlinedButton(
                    onClick = { sudokuViewModel.clearUserInputs() }
                ) {
                    Icon(Icons.Default.Clear, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear")
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