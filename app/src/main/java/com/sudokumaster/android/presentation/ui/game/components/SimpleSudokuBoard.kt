package com.sudokumaster.android.presentation.ui.game.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudokumaster.android.domain.model.CellPosition
import com.sudokumaster.android.domain.model.SudokuGrid

@Composable
fun SimpleSudokuBoard(
    grid: SudokuGrid,
    originalGrid: SudokuGrid,
    selectedCell: CellPosition?,
    errors: Map<CellPosition, Boolean>,
    hintCell: Triple<Int, Int, Int>?,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Main 9x9 grid with outer border
    Column(
        modifier = modifier
            .border(2.dp, Color.White, RoundedCornerShape(4.dp))
            .background(Color.Black, RoundedCornerShape(4.dp))
            .padding(2.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        // 3x3 grid of sub-grids
        repeat(3) { boxRow ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                repeat(3) { boxCol ->
                    // Each 3x3 sub-grid
                    SubGrid(
                        grid = grid,
                        originalGrid = originalGrid,
                        selectedCell = selectedCell,
                        errors = errors,
                        hintCell = hintCell,
                        onCellClick = onCellClick,
                        boxRow = boxRow,
                        boxCol = boxCol,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SubGrid(
    grid: SudokuGrid,
    originalGrid: SudokuGrid,
    selectedCell: CellPosition?,
    errors: Map<CellPosition, Boolean>,
    hintCell: Triple<Int, Int, Int>?,
    onCellClick: (Int, Int) -> Unit,
    boxRow: Int,
    boxCol: Int,
    modifier: Modifier = Modifier
) {
    // 3x3 sub-grid with border
    Column(
        modifier = modifier
            .border(1.dp, Color.White)
            .background(Color.Black)
            .padding(1.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        repeat(3) { innerRow ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                repeat(3) { innerCol ->
                    val row = boxRow * 3 + innerRow
                    val col = boxCol * 3 + innerCol
                    val cellPosition = CellPosition(row, col)
                    val isSelected = selectedCell?.row == row && selectedCell?.col == col
                    val hasError = errors[cellPosition] == true
                    val isOriginal = originalGrid[row][col] != null
                    val isHint = hintCell?.let { it.first == row && it.second == col } == true
                    val value = grid[row][col]
                    
                    SimpleSudokuCell(
                        value = value,
                        isSelected = isSelected,
                        hasError = hasError,
                        isOriginal = isOriginal,
                        isHint = isHint,
                        hintValue = if (isHint) hintCell?.third else null,
                        onClick = { onCellClick(row, col) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleSudokuCell(
    value: Int?,
    isSelected: Boolean,
    hasError: Boolean,
    isOriginal: Boolean,
    isHint: Boolean,
    hintValue: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    
    val backgroundColor = when {
        hasError -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        isHint -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surface
    }
    
    val textColor = when {
        hasError -> MaterialTheme.colorScheme.error
        isOriginal -> MaterialTheme.colorScheme.onSurface
        isHint -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }
    
    Card(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        modifier = modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val displayValue = when {
                isHint && hintValue != null -> hintValue.toString()
                value != null -> value.toString()
                else -> ""
            }
            
            if (displayValue.isNotEmpty()) {
                Text(
                    text = displayValue,
                    fontSize = 18.sp,
                    fontWeight = if (isOriginal) FontWeight.Bold else FontWeight.Normal,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}