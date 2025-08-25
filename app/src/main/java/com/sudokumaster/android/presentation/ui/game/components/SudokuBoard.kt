package com.sudokumaster.android.presentation.ui.game.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudokumaster.android.domain.model.CellPosition
import com.sudokumaster.android.domain.model.SudokuGrid

@Composable
fun SudokuBoard(
    grid: SudokuGrid,
    originalGrid: SudokuGrid,
    selectedCell: CellPosition?,
    errors: Map<CellPosition, Boolean>,
    hintCell: Triple<Int, Int, Int>?,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            repeat(9) { row ->
                Row {
                    repeat(9) { col ->
                        val cellPosition = CellPosition(row, col)
                        val isSelected = selectedCell?.row == row && selectedCell?.col == col
                        val hasError = errors[cellPosition] == true
                        val isOriginal = originalGrid[row][col] != null
                        val isHint = hintCell?.let { it.first == row && it.second == col } == true
                        
                        SudokuCell(
                            value = grid[row][col],
                            isSelected = isSelected,
                            hasError = hasError,
                            isOriginal = isOriginal,
                            isHint = isHint,
                            hintValue = if (isHint) hintCell?.third else null,
                            onClick = { onCellClick(row, col) },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(
                                    end = if (col == 2 || col == 5) 4.dp else 1.dp,
                                    bottom = if (row == 2 || row == 5) 4.dp else 1.dp
                                )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuCell(
    value: Int?,
    isSelected: Boolean,
    hasError: Boolean,
    isOriginal: Boolean,
    isHint: Boolean,
    hintValue: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        hasError -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        isHint -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.surface
    }
    
    val textColor = when {
        hasError -> MaterialTheme.colorScheme.error
        isOriginal -> MaterialTheme.colorScheme.onSurface
        isHint -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }
    
    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        hasError -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }
    
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Card(
        onClick = onClick,
        modifier = modifier
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(4.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(4.dp)
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
            
            Text(
                text = displayValue,
                fontSize = 20.sp,
                fontWeight = if (isOriginal) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}