package com.sudokumaster.android.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Material You color system for Sudoku Master
 * Optimized for Pixel 10 Pro with enhanced contrast and accessibility
 */

// Primary color palette - Sudoku brand purple/blue
val SudokuPrimary = Color(0xFF6750A4)
val SudokuOnPrimary = Color(0xFFFFFFFF)
val SudokuPrimaryContainer = Color(0xFFE9DDFF)
val SudokuOnPrimaryContainer = Color(0xFF22005D)

// Secondary color palette - Complementary purple
val SudokuSecondary = Color(0xFF625B71)
val SudokuOnSecondary = Color(0xFFFFFFFF)
val SudokuSecondaryContainer = Color(0xFFE8DEF8)
val SudokuOnSecondaryContainer = Color(0xFF1E192B)

// Tertiary color palette - Accent pink for highlights
val SudokuTertiary = Color(0xFF7D5260)
val SudokuOnTertiary = Color(0xFFFFFFFF)
val SudokuTertiaryContainer = Color(0xFFFFD8E4)
val SudokuOnTertiaryContainer = Color(0xFF31111D)

// Error colors for mistakes and validation
val SudokuError = Color(0xFFBA1A1A)
val SudokuOnError = Color(0xFFFFFFFF)
val SudokuErrorContainer = Color(0xFFFFDAD6)
val SudokuOnErrorContainer = Color(0xFF410002)

// Success colors for completed puzzles
val SudokuSuccess = Color(0xFF00696B)
val SudokuOnSuccess = Color(0xFFFFFFFF)
val SudokuSuccessContainer = Color(0xFF9FF7F9)
val SudokuOnSuccessContainer = Color(0xFF002021)

// Neutral colors for backgrounds and surfaces
val SudokuNeutral10 = Color(0xFF1C1B1F)
val SudokuNeutral20 = Color(0xFF313033)
val SudokuNeutral90 = Color(0xFFE6E1E5)
val SudokuNeutral95 = Color(0xFFF4EFF4)
val SudokuNeutral99 = Color(0xFFFFFBFE)

val SudokuNeutralVariant30 = Color(0xFF49454F)
val SudokuNeutralVariant50 = Color(0xFF79747E)
val SudokuNeutralVariant60 = Color(0xFF938F99)
val SudokuNeutralVariant80 = Color(0xFFCAC4D0)
val SudokuNeutralVariant90 = Color(0xFFE7E0EC)

/**
 * Light color scheme for Material You implementation
 */
val LightColorScheme = lightColorScheme(
    primary = SudokuPrimary,
    onPrimary = SudokuOnPrimary,
    primaryContainer = SudokuPrimaryContainer,
    onPrimaryContainer = SudokuOnPrimaryContainer,
    
    secondary = SudokuSecondary,
    onSecondary = SudokuOnSecondary,
    secondaryContainer = SudokuSecondaryContainer,
    onSecondaryContainer = SudokuOnSecondaryContainer,
    
    tertiary = SudokuTertiary,
    onTertiary = SudokuOnTertiary,
    tertiaryContainer = SudokuTertiaryContainer,
    onTertiaryContainer = SudokuOnTertiaryContainer,
    
    error = SudokuError,
    onError = SudokuOnError,
    errorContainer = SudokuErrorContainer,
    onErrorContainer = SudokuOnErrorContainer,
    
    background = SudokuNeutral99,
    onBackground = SudokuNeutral10,
    
    surface = SudokuNeutral99,
    onSurface = SudokuNeutral10,
    surfaceVariant = SudokuNeutralVariant90,
    onSurfaceVariant = SudokuNeutralVariant30,
    
    outline = SudokuNeutralVariant50,
    outlineVariant = SudokuNeutralVariant80,
    
    inverseSurface = SudokuNeutral20,
    inverseOnSurface = SudokuNeutral95,
    inversePrimary = Color(0xFFD0BCFF)
)

/**
 * Dark color scheme for Material You implementation
 */
val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = SudokuPrimaryContainer,
    
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = SudokuSecondaryContainer,
    
    tertiary = Color(0xFFEFB8C8),
    onTertiary = Color(0xFF492532),
    tertiaryContainer = Color(0xFF633B48),
    onTertiaryContainer = SudokuTertiaryContainer,
    
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = SudokuErrorContainer,
    
    background = SudokuNeutral10,
    onBackground = SudokuNeutral90,
    
    surface = SudokuNeutral10,
    onSurface = SudokuNeutral90,
    surfaceVariant = SudokuNeutralVariant30,
    onSurfaceVariant = SudokuNeutralVariant80,
    
    outline = SudokuNeutralVariant60,
    outlineVariant = SudokuNeutralVariant30,
    
    inverseSurface = SudokuNeutral90,
    inverseOnSurface = SudokuNeutral20,
    inversePrimary = SudokuPrimary
)

/**
 * Custom colors for Sudoku-specific UI elements
 */
val SudokuCustomColors = mapOf(
    "originalNumber" to Color(0xFF1C1B1F),
    "userNumber" to Color(0xFF6750A4),
    "hintNumber" to Color(0xFF7D5260),
    "errorNumber" to Color(0xFFBA1A1A),
    "selectedCell" to Color(0xFFE9DDFF),
    "relatedCell" to Color(0xFFF4EFF4),
    "completedRow" to Color(0xFF9FF7F9),
    "gameComplete" to Color(0xFF00696B)
)