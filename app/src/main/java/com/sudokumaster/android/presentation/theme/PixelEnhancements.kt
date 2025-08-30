package com.sudokumaster.android.presentation.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Pixel 10 Pro specific enhancements for optimal user experience
 * Includes display cutout handling, enhanced animations, and Pixel-specific features
 */
object PixelEnhancements {
    
    /**
     * Enhanced animation specs optimized for Pixel's 120Hz display
     */
    object Animations {
        val FastSpring = spring<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
        
        val SmoothSpring = spring<Float>(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )
        
        val QuickFade = tween<Float>(
            durationMillis = 150,
            easing = FastOutSlowInEasing
        )
        
        val SmoothScale = spring<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
        
        val ButtonPress = spring<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        )
        
        // Pixel-specific haptic timing
        val HapticDelay = 50 // milliseconds
    }
    
    /**
     * Display-aware spacing that adapts to Pixel 10 Pro's dimensions
     */
    @Composable
    fun getAdaptiveSpacing(): AdaptiveSpacing {
        val configuration = LocalConfiguration.current
        val density = LocalDensity.current
        
        val screenWidthDp = configuration.screenWidthDp.dp
        val screenHeightDp = configuration.screenHeightDp.dp
        
        return when {
            screenWidthDp >= 400.dp -> AdaptiveSpacing.Large
            screenWidthDp >= 360.dp -> AdaptiveSpacing.Medium
            else -> AdaptiveSpacing.Compact
        }
    }
    
    sealed class AdaptiveSpacing(
        val small: Dp,
        val medium: Dp,
        val large: Dp,
        val extraLarge: Dp
    ) {
        object Compact : AdaptiveSpacing(4.dp, 8.dp, 12.dp, 16.dp)
        object Medium : AdaptiveSpacing(6.dp, 12.dp, 18.dp, 24.dp)
        object Large : AdaptiveSpacing(8.dp, 16.dp, 24.dp, 32.dp)
    }
    
    /**
     * Pixel-optimized shapes with rounded display consideration
     */
    object PixelShapes {
        val FullscreenCard = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 32.dp, // Matches Pixel's rounded corners
            bottomEnd = 32.dp
        )
        
        val FloatingCard = RoundedCornerShape(28.dp)
        val InteractiveButton = RoundedCornerShape(24.dp)
        val CompactButton = RoundedCornerShape(20.dp)
        val GameBoard = RoundedCornerShape(32.dp)
        val GameCell = RoundedCornerShape(12.dp)
        val NumberPad = RoundedCornerShape(28.dp)
    }
    
    /**
     * Enhanced colors optimized for Pixel display technology
     */
    object PixelColors {
        // High contrast colors for OLED optimization
        val TrueBlack = Color(0xFF000000)
        val RichWhite = Color(0xFFFFFBFE)
        
        // Vibrant accent colors that pop on Pixel displays
        val PixelBlue = Color(0xFF4285F4)
        val PixelGreen = Color(0xFF34A853)
        val PixelYellow = Color(0xFFFBBC04)
        val PixelRed = Color(0xFFEA4335)
        
        // Gaming-optimized colors
        val GameSuccess = Color(0xFF00C853)
        val GameWarning = Color(0xFFFF9800)
        val GameError = Color(0xFFD32F2F)
        val GameHint = Color(0xFF9C27B0)
    }
    
    /**
     * Pixel-specific elevation system for optimal shadow rendering
     */
    object Elevations {
        val Surface = 0.dp
        val Card = 1.dp
        val Button = 3.dp
        val FloatingButton = 6.dp
        val NavigationBar = 3.dp
        val Modal = 24.dp
        val GameBoard = 8.dp
        val SelectedCell = 12.dp
    }
    
    /**
     * Typography adjustments for Pixel's display density
     */
    @Composable
    fun getPixelTypographyScale(): Float {
        val configuration = LocalConfiguration.current
        val density = LocalDensity.current
        
        return when {
            configuration.densityDpi >= 560 -> 1.1f // XXXHDPI+
            configuration.densityDpi >= 480 -> 1.05f // XXHDPI
            else -> 1.0f
        }
    }
}

/**
 * Pixel-specific motion design patterns
 */
object PixelMotion {
    // Staggered animation delays for list items
    fun staggerDelay(index: Int): Int = index * 50
    
    // Emphasis animations for important actions
    val EmphasizeScale = keyframes<Float> {
        durationMillis = 400
        1.0f at 0 with FastOutSlowInEasing
        1.1f at 100 with FastOutSlowInEasing
        1.05f at 200 with FastOutSlowInEasing
        1.0f at 400 with FastOutSlowInEasing
    }
    
    // Subtle breathing animation for idle states
    val BreathingPulse = infiniteRepeatable<Float>(
        animation = tween(2000, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
    )
}