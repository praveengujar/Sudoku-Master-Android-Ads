package com.sudokumaster.android.presentation.ui.game

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBarsPadding
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
import com.sudokumaster.android.presentation.theme.SudokuCustomShapes
import com.sudokumaster.android.presentation.theme.PixelEnhancements
import com.sudokumaster.android.presentation.theme.PixelMotion
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sudokumaster.android.domain.model.SudokuDifficulty
import com.sudokumaster.android.presentation.ui.game.components.*
import com.sudokumaster.android.presentation.viewmodel.AuthViewModel
import com.sudokumaster.android.presentation.viewmodel.SudokuGameViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    authViewModel: AuthViewModel,
    onNavigateToAuth: () -> Unit,
    onNavigateToProfile: () -> Unit,
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
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        // Sudoku Master branding at the top
        Text(
            text = "Sudoku Master",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        
        // Simple header with username and timer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Username with icon and offline badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        onNavigateToProfile()
                    }
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = if (isGuestMode) "Guest" else (currentUser?.username ?: "Player"),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                if (isOfflineMode) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ) {
                        Text(
                            text = "Offline",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            // Timer with icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = formatTime(timeSpentSeconds),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Main content area
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                        Text(
                            text = "Loading puzzle...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                // Clean Sudoku board matching reference design
                SimpleSudokuBoard(
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
                
                // Action icons row (like in reference)
                ActionIconsRow(
                    currentDifficulty = difficulty,
                    onDifficultyChange = { newDifficulty ->
                        sudokuViewModel.setDifficulty(newDifficulty)
                    },
                    onRedo = { sudokuViewModel.redo() },
                    onHint = { sudokuViewModel.getHint() },
                    onAutoSolve = { sudokuViewModel.autoSolve() }
                )
                
                // Clean number pad - more compact
                CleanNumberPad(
                    onNumberClick = { number ->
                        sudokuViewModel.enterNumber(number)
                    },
                    onEraseClick = {
                        sudokuViewModel.eraseNumber()
                    }
                )
            }
        }
        
        // Banner Ad Placeholder
        BannerAdPlaceholder()
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

@Composable
private fun BannerAdPlaceholder() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        shape = RoundedCornerShape(8.dp)
    ) {
        // Empty space reserved for banner ad
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", minutes, secs)
}