package com.sudokumaster.android.presentation.ui.game.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudokumaster.android.domain.model.CellPosition
import com.sudokumaster.android.domain.model.SudokuGrid
import com.sudokumaster.android.presentation.theme.SudokuCustomShapes
import com.sudokumaster.android.presentation.theme.SudokuTextStyles

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
    ElevatedCard(
        modifier = modifier
            .shadow(
                elevation = 16.dp,
                shape = SudokuCustomShapes.ExpressiveSudokuBoard,
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp),
        shape = SudokuCustomShapes.ExpressiveSudokuBoard,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Create 3x3 grid of boxes for visual separation
            repeat(3) { boxRow ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) { boxCol ->
                        SudokuBox(
                            boxRow = boxRow,
                            boxCol = boxCol,
                            grid = grid,
                            originalGrid = originalGrid,
                            selectedCell = selectedCell,
                            errors = errors,
                            hintCell = hintCell,
                            onCellClick = onCellClick,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SudokuBox(
    boxRow: Int,
    boxCol: Int,
    grid: SudokuGrid,
    originalGrid: SudokuGrid,
    selectedCell: CellPosition?,
    errors: Map<CellPosition, Boolean>,
    hintCell: Triple<Int, Int, Int>?,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                shape = SudokuCustomShapes.ExpressiveSudokuBox
            ),
        shape = SudokuCustomShapes.ExpressiveSudokuBox,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            repeat(3) { innerRow ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    repeat(3) { innerCol ->
                        val row = boxRow * 3 + innerRow
                        val col = boxCol * 3 + innerCol
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
    val haptic = LocalHapticFeedback.current
    
    // Animated properties
    val backgroundColor by animateColorAsState(
        targetValue = when {
            hasError -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
            isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
            isHint -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f)
            else -> MaterialTheme.colorScheme.surfaceContainerHigh
        },
        animationSpec = tween(200),
        label = "cell_background_color"
    )
    
    val textColor by animateColorAsState(
        targetValue = when {
            hasError -> MaterialTheme.colorScheme.onErrorContainer
            isOriginal -> MaterialTheme.colorScheme.onSurface
            isHint -> MaterialTheme.colorScheme.onTertiaryContainer
            isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(200),
        label = "cell_text_color"
    )
    
    val borderColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primary
            hasError -> MaterialTheme.colorScheme.error
            else -> Color.Transparent
        },
        animationSpec = tween(200),
        label = "cell_border_color"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 2.dp,
        animationSpec = tween(200),
        label = "cell_elevation"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cell_scale"
    )

    ElevatedCard(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(
                width = if (isSelected || hasError) 2.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = elevation
        ),
        shape = if (isSelected) SudokuCustomShapes.ExpressiveSudokuCell else SudokuCustomShapes.SudokuCell
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
                    style = when {
                        isOriginal -> SudokuTextStyles.SudokuNumber.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        isHint -> SudokuTextStyles.SudokuHint.copy(
                            fontSize = 16.sp
                        )
                        else -> SudokuTextStyles.SudokuNumber.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp
                        )
                    },
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}