package com.sudokumaster.android.domain.repository

import com.sudokumaster.android.domain.model.*

interface SudokuRepository {
    suspend fun generatePuzzle(difficulty: SudokuDifficulty): SudokuPuzzle
    suspend fun validateMove(grid: SudokuGrid, row: Int, col: Int, value: Int): Boolean
    suspend fun solvePuzzle(grid: SudokuGrid): SudokuGrid
    suspend fun saveGameProgress(
        userId: Int,
        puzzleId: Int,
        currentGrid: SudokuGrid,
        isCompleted: Boolean,
        timeSpentSeconds: Int
    ): GameplayRecord
    suspend fun getUserStats(userId: Int): UserStats
    
    // Offline functionality
    suspend fun getOfflinePuzzle(difficulty: SudokuDifficulty): SudokuPuzzle?
    suspend fun saveOfflinePuzzle(puzzle: SudokuPuzzle)
    suspend fun getOfflinePuzzles(difficulty: SudokuDifficulty): List<SudokuPuzzle>
    suspend fun saveLocalProgress(record: StoredGameRecord)
    suspend fun getLocalProgress(): List<StoredGameRecord>
    suspend fun clearExpiredPuzzles()
}