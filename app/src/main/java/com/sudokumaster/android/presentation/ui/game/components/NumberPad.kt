package com.sudokumaster.android.presentation.ui.game.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NumberPad(
    onNumberClick: (Int) -> Unit,
    onEraseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // First row: 1, 2, 3, 4, 5
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(5) { index ->
                    NumberButton(
                        number = index + 1,
                        onClick = { onNumberClick(index + 1) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Second row: 6, 7, 8, 9, Erase
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(4) { index ->
                    NumberButton(
                        number = index + 6,
                        onClick = { onNumberClick(index + 6) },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Erase button
                EraseButton(
                    onClick = onEraseClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NumberButton(
    number: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "button_scale"
    )
    
    val containerColor by animateColorAsState(
        targetValue = if (isPressed) 
            MaterialTheme.colorScheme.primary 
        else 
            MaterialTheme.colorScheme.primaryContainer,
        animationSpec = tween(100),
        label = "button_color"
    )
    
    FilledTonalButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier
            .aspectRatio(1f)
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = containerColor,
            contentColor = if (isPressed) 
                MaterialTheme.colorScheme.onPrimary 
            else 
                MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = ButtonDefaults.filledTonalButtonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = number.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun EraseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "erase_button_scale"
    )
    
    val containerColor by animateColorAsState(
        targetValue = if (isPressed) 
            MaterialTheme.colorScheme.error 
        else 
            MaterialTheme.colorScheme.errorContainer,
        animationSpec = tween(100),
        label = "erase_button_color"
    )
    
    OutlinedButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier
            .aspectRatio(1f)
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = if (isPressed) 
                MaterialTheme.colorScheme.onError 
            else 
                MaterialTheme.colorScheme.onErrorContainer
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 2.dp
        ),
        elevation = null,
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            Icons.AutoMirrored.Default.Backspace,
            contentDescription = "Erase",
            modifier = Modifier.size(20.dp)
        )
    }
}