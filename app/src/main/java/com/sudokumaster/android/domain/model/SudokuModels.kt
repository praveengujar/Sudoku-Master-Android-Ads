package com.sudokumaster.android.domain.model

import androidx.compose.ui.graphics.Color
import java.util.Date

// Sudoku grid represented as 9x9 array of nullable integers
typealias SudokuGrid = Array<Array<Int?>>

// Difficulty levels for Sudoku puzzles
enum class SudokuDifficulty(val value: String) {
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard");

    val displayName: String
        get() = when (this) {
            EASY -> "Easy"
            MEDIUM -> "Medium"
            HARD -> "Hard"
        }

    val cellsToRemove: Int
        get() = when (this) {
            EASY -> 35  // Shows 46 cells
            MEDIUM -> 45  // Shows 36 cells
            HARD -> 55  // Shows 26 cells
        }

    val color: Color
        get() = when (this) {
            EASY -> Color(0xFF4CAF50)  // Green
            MEDIUM -> Color(0xFFFF9800)  // Orange
            HARD -> Color(0xFFF44336)  // Red
        }

    val iconResource: String
        get() = when (this) {
            EASY -> "ic_easy"
            MEDIUM -> "ic_medium"
            HARD -> "ic_hard"
        }

    val expectedCompletionTime: Int
        get() = when (this) {
            EASY -> 300  // 5 minutes
            MEDIUM -> 600  // 10 minutes
            HARD -> 1200 // 20 minutes
        }

    val errorTolerance: Double
        get() = when (this) {
            EASY -> 0.4  // 40% error tolerance
            MEDIUM -> 0.3  // 30% error tolerance
            HARD -> 0.2  // 20% error tolerance
        }

    val skillRange: IntRange
        get() = when (this) {
            EASY -> 0..35
            MEDIUM -> 36..70
            HARD -> 71..100
        }

    val challengeScore: Int
        get() = when (this) {
            EASY -> 1
            MEDIUM -> 3
            HARD -> 6
        }

    fun isAppropriateFor(skillLevel: Int): Boolean {
        return skillLevel in skillRange
    }

    val learningTips: List<String>
        get() = when (this) {
            EASY -> listOf(
                "Focus on scanning techniques to identify obvious placements",
                "Look for rows, columns, or boxes with many filled cells",
                "Practice the single candidate technique"
            )
            MEDIUM -> listOf(
                "Learn about 'candidate pairs' to eliminate possibilities",
                "Use the 'pointing pair' technique to narrow down options",
                "Practice 'box/line reduction' for more complex puzzles"
            )
            HARD -> listOf(
                "Master X-Wing and Y-Wing techniques for complex eliminations",
                "Use 'forcing chains' to identify contradictions",
                "Try the 'Swordfish' technique for advanced puzzles"
            )
        }

    val progressionHint: String
        get() = when (this) {
            EASY -> "Try to improve your solving time and minimize errors to advance to Medium difficulty."
            MEDIUM -> "Practice recognizing advanced patterns and techniques to prepare for Hard puzzles."
            HARD -> "Continue mastering advanced techniques to improve your Sudoku mastery."
        }

    companion object {
        fun getSkillDescription(skillLevel: Int): String {
            return when (skillLevel) {
                in 0..20 -> "Beginner"
                in 21..40 -> "Casual Player"
                in 41..60 -> "Intermediate"
                in 61..80 -> "Advanced"
                in 81..90 -> "Expert"
                in 91..100 -> "Master"
                else -> "Unknown"
            }
        }

        fun fromString(value: String): SudokuDifficulty {
            return values().find { it.value == value } ?: EASY
        }
    }
}

// A cell position in the Sudoku grid
data class CellPosition(
    val row: Int,
    val col: Int
)

// Represents a complete Sudoku puzzle
data class SudokuPuzzle(
    val id: Int,
    val grid: SudokuGrid,
    val solution: SudokuGrid,
    val difficulty: SudokuDifficulty
) {
    val puzzleId: String
        get() = "${id}-${difficulty.value}"
}

// Theme options
enum class ThemeOption(val value: String) {
    DEFAULT("default"),
    STARWARS("starwars"),
    MINECRAFT("minecraft"),
    MARIO("mario");

    val displayName: String
        get() = when (this) {
            DEFAULT -> "Default"
            STARWARS -> "Star Wars"
            MINECRAFT -> "Minecraft"
            MARIO -> "Mario"
        }

    companion object {
        fun fromString(value: String): ThemeOption {
            return values().find { it.value == value } ?: DEFAULT
        }
    }
}

// User model
data class User(
    val id: Int,
    val username: String,
    val theme: ThemeOption
)

// JWT Authentication Response models
data class AuthResponse(
    val user: User,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class RefreshTokenResponse(
    val accessToken: String,
    val expiresIn: String
)

data class LogoutRequest(
    val refreshToken: String
)

// User statistics
data class UserStats(
    val id: Int,
    val userId: Int,
    val eloRating: Int,
    val gamesPlayed: Int,
    val gamesWon: Int,
    val averageTimeSeconds: Int
) {
    val normalizedRating: Int
        get() {
            val minElo = 400.0 // Minimum possible Elo
            val maxElo = 2000.0 // Maximum expected Elo
            val normalized = ((eloRating - minElo) / (maxElo - minElo)) * 100
            return kotlin.math.min(100.0, kotlin.math.max(1.0, normalized)).toInt()
        }
}

// Represents an ongoing game
data class GameplayRecord(
    val id: Int,
    val userId: Int,
    val puzzleId: Int,
    val currentGrid: SudokuGrid,
    val isCompleted: Boolean,
    val timeSpentSeconds: Int,
    val createdAt: Date
) {
    val localId: String
        get() = "$puzzleId-$userId"
}

// Saved puzzle for later play
data class SavedPuzzle(
    val id: Int,
    val userId: Int,
    val puzzleId: Int,
    val puzzleName: String,
    val currentGrid: SudokuGrid,
    val originalGrid: SudokuGrid,
    val difficulty: SudokuDifficulty,
    val createdAt: Date
)

// Model for storing game progress in local storage
data class StoredGameRecord(
    val puzzleId: Int,
    val userId: Int?,
    val currentGrid: SudokuGrid,
    val originalGrid: SudokuGrid,
    val difficulty: SudokuDifficulty,
    val isCompleted: Boolean,
    val timeSpentSeconds: Int,
    val timestamp: Date
)

// Model for saved custom puzzle in local storage
data class SavedCustomPuzzle(
    val id: Int,
    val puzzleName: String,
    val originalGrid: SudokuGrid,
    val currentGrid: SudokuGrid,
    val difficulty: SudokuDifficulty,
    val createdAt: Date
)

// Model for app settings
data class AppSettings(
    val theme: ThemeOption,
    val soundEnabled: Boolean,
    val hapticEnabled: Boolean
)