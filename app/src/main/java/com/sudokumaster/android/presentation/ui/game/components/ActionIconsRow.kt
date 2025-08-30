package com.sudokumaster.android.presentation.ui.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun ActionIconsRow(
    currentDifficulty: com.sudokumaster.android.domain.model.SudokuDifficulty,
    onDifficultyChange: (com.sudokumaster.android.domain.model.SudokuDifficulty) -> Unit,
    onRedo: () -> Unit,
    onHint: () -> Unit,
    onAutoSolve: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Difficulty Level Icons (First 3)
        DifficultyIconButton(
            difficulty = com.sudokumaster.android.domain.model.SudokuDifficulty.EASY,
            isSelected = currentDifficulty == com.sudokumaster.android.domain.model.SudokuDifficulty.EASY,
            onClick = { onDifficultyChange(com.sudokumaster.android.domain.model.SudokuDifficulty.EASY) }
        )
        
        DifficultyIconButton(
            difficulty = com.sudokumaster.android.domain.model.SudokuDifficulty.MEDIUM,
            isSelected = currentDifficulty == com.sudokumaster.android.domain.model.SudokuDifficulty.MEDIUM,
            onClick = { onDifficultyChange(com.sudokumaster.android.domain.model.SudokuDifficulty.MEDIUM) }
        )
        
        DifficultyIconButton(
            difficulty = com.sudokumaster.android.domain.model.SudokuDifficulty.HARD,
            isSelected = currentDifficulty == com.sudokumaster.android.domain.model.SudokuDifficulty.HARD,
            onClick = { onDifficultyChange(com.sudokumaster.android.domain.model.SudokuDifficulty.HARD) }
        )
        
        // Vertical Separator
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(32.dp)
                .background(Color.Gray.copy(alpha = 0.5f))
        )
        
        // Action Icons (Next 3)
        ActionIconButton(
            icon = Icons.AutoMirrored.Filled.Redo,
            backgroundColor = Color(0xFF2196F3), // Blue
            onClick = onRedo
        )
        
        ActionIconButton(
            icon = Icons.Default.Lightbulb,
            backgroundColor = Color(0xFF795548), // Brown
            onClick = onHint
        )
        
        ActionIconButton(
            icon = Icons.Default.Bolt,
            backgroundColor = Color(0xFFFF9800), // Orange  
            onClick = onAutoSolve
        )
    }
}

@Composable
private fun DifficultyIconButton(
    difficulty: com.sudokumaster.android.domain.model.SudokuDifficulty,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    
    val (backgroundColor, textColor) = when (difficulty) {
        com.sudokumaster.android.domain.model.SudokuDifficulty.EASY -> 
            if (isSelected) Color(0xFF4CAF50) to Color.White else Color(0xFF4CAF50).copy(alpha = 0.3f) to Color(0xFF4CAF50)
        com.sudokumaster.android.domain.model.SudokuDifficulty.MEDIUM -> 
            if (isSelected) Color(0xFFFF9800) to Color.White else Color(0xFFFF9800).copy(alpha = 0.3f) to Color(0xFFFF9800)
        com.sudokumaster.android.domain.model.SudokuDifficulty.HARD -> 
            if (isSelected) Color(0xFFF44336) to Color.White else Color(0xFFF44336).copy(alpha = 0.3f) to Color(0xFFF44336)
    }
    
    FilledIconButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        modifier = modifier.size(48.dp),
        shape = CircleShape,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        )
    ) {
        Text(
            text = when (difficulty) {
                com.sudokumaster.android.domain.model.SudokuDifficulty.EASY -> "E"
                com.sudokumaster.android.domain.model.SudokuDifficulty.MEDIUM -> "M" 
                com.sudokumaster.android.domain.model.SudokuDifficulty.HARD -> "H"
            },
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ActionIconButton(
    icon: ImageVector,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    
    FilledIconButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        modifier = modifier.size(48.dp),
        shape = CircleShape,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = backgroundColor,
            contentColor = Color.White
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}