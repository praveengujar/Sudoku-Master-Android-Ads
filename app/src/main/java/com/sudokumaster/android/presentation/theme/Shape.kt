package com.sudokumaster.android.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Material You Shape System for Sudoku Master
 * Optimized for Pixel 10 Pro with modern, rounded aesthetics
 */

/**
 * Standard Material 3 Shapes
 */
val SudokuShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

/**
 * Expressive Material 3 Shapes for enhanced gaming experience
 * More pronounced curves for better visual appeal
 */
val SudokuExpressiveShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

/**
 * Sudoku-specific shapes for game components
 */
object SudokuCustomShapes {
    // Sudoku board and cells
    val SudokuBoard = RoundedCornerShape(24.dp)
    val SudokuBox = RoundedCornerShape(16.dp)
    val SudokuCell = RoundedCornerShape(8.dp)
    val SudokuCellSelected = RoundedCornerShape(12.dp)
    
    // UI components
    val NumberPad = RoundedCornerShape(20.dp)
    val NumberButton = RoundedCornerShape(16.dp)
    val ActionButton = RoundedCornerShape(16.dp)
    val InfoCard = RoundedCornerShape(16.dp)
    val ModalDialog = RoundedCornerShape(28.dp)
    
    // Expressive variants for enhanced visual appeal
    val ExpressiveSudokuBoard = RoundedCornerShape(28.dp)
    val ExpressiveSudokuBox = RoundedCornerShape(20.dp)
    val ExpressiveSudokuCell = RoundedCornerShape(12.dp)
    val ExpressiveNumberPad = RoundedCornerShape(24.dp)
    val ExpressiveNumberButton = RoundedCornerShape(20.dp)
    val ExpressiveActionButton = RoundedCornerShape(20.dp)
    val ExpressiveInfoCard = RoundedCornerShape(20.dp)
    val ExpressiveModalDialog = RoundedCornerShape(32.dp)
    
    // Pixel-specific shapes optimized for rounded display
    val PixelFullScreen = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart = 32.dp,
        bottomEnd = 32.dp
    )
    
    val PixelBottomSheet = RoundedCornerShape(
        topStart = 28.dp,
        topEnd = 28.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
    
    val PixelCard = RoundedCornerShape(16.dp)
    val PixelButton = RoundedCornerShape(24.dp) // More rounded for Pixel aesthetic
}