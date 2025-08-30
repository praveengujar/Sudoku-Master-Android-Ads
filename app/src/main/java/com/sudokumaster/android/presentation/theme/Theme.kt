package com.sudokumaster.android.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Material You (Material Design 3) theme for Sudoku Master
 * Optimized for Pixel devices with dynamic color support
 */
@Composable
fun SudokuMasterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SudokuTypography,
        shapes = SudokuShapes,
        content = content
    )
}

/**
 * Expressive Material You theme variant for enhanced visual appeal
 * Perfect for gaming apps with more vibrant colors and emphasis
 */
@Composable
fun SudokuMasterExpressiveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val baseColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    // Create expressive variant with enhanced colors for gaming experience
    val expressiveColorScheme = if (darkTheme) {
        baseColorScheme.copy(
            primary = baseColorScheme.primary,
            primaryContainer = baseColorScheme.primaryContainer.copy(alpha = 0.9f),
            secondary = baseColorScheme.secondary,
            secondaryContainer = baseColorScheme.secondaryContainer.copy(alpha = 0.8f),
            tertiary = baseColorScheme.tertiary,
            tertiaryContainer = baseColorScheme.tertiaryContainer.copy(alpha = 0.8f),
            surface = baseColorScheme.surface,
            surfaceContainer = baseColorScheme.surfaceContainer.copy(alpha = 0.95f),
            surfaceContainerHigh = baseColorScheme.surfaceContainerHigh.copy(alpha = 0.9f),
            surfaceContainerHighest = baseColorScheme.surfaceContainerHighest.copy(alpha = 0.85f)
        )
    } else {
        baseColorScheme.copy(
            primary = baseColorScheme.primary,
            primaryContainer = baseColorScheme.primaryContainer.copy(alpha = 0.9f),
            secondary = baseColorScheme.secondary,
            secondaryContainer = baseColorScheme.secondaryContainer.copy(alpha = 0.8f),
            tertiary = baseColorScheme.tertiary,
            tertiaryContainer = baseColorScheme.tertiaryContainer.copy(alpha = 0.8f),
            surface = baseColorScheme.surface,
            surfaceContainer = baseColorScheme.surfaceContainer.copy(alpha = 0.95f),
            surfaceContainerHigh = baseColorScheme.surfaceContainerHigh.copy(alpha = 0.9f),
            surfaceContainerHighest = baseColorScheme.surfaceContainerHighest.copy(alpha = 0.85f)
        )
    }

    MaterialTheme(
        colorScheme = expressiveColorScheme,
        typography = SudokuExpressiveTypography,
        shapes = SudokuExpressiveShapes,
        content = content
    )
}