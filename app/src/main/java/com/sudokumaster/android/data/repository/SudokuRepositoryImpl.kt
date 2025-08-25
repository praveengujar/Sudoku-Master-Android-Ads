package com.sudokumaster.android.data.repository

import com.sudokumaster.android.data.local.SudokuDatabase
import com.sudokumaster.android.data.local.OfflinePuzzleEntity
import com.sudokumaster.android.data.local.GameProgressEntity
import com.sudokumaster.android.data.remote.ApiService
import com.sudokumaster.android.data.remote.SaveProgressRequest
import com.sudokumaster.android.data.remote.SolvePuzzleRequest
import com.sudokumaster.android.data.remote.ValidateMoveRequest
import com.sudokumaster.android.domain.model.*
import com.sudokumaster.android.domain.repository.AuthRepository
import com.sudokumaster.android.domain.repository.SudokuRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SudokuRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val database: SudokuDatabase,
    private val authRepository: AuthRepository
) : SudokuRepository {

    override suspend fun generatePuzzle(difficulty: SudokuDifficulty): SudokuPuzzle {
        return withContext(Dispatchers.IO) {
            val response = apiService.generatePuzzle(difficulty.value)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response from server")
            } else {
                throw Exception("Failed to generate puzzle: ${response.code()} ${response.message()}")
            }
        }
    }

    override suspend fun validateMove(grid: SudokuGrid, row: Int, col: Int, value: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val request = ValidateMoveRequest(grid, row, col, value)
                val response = apiService.validateMove(request)
                if (response.isSuccessful) {
                    response.body()?.isValid ?: false
                } else {
                    // Fallback to local validation if API fails
                    validateMoveLocally(grid, row, col, value)
                }
            } catch (e: Exception) {
                // Fallback to local validation
                validateMoveLocally(grid, row, col, value)
            }
        }
    }

    override suspend fun solvePuzzle(grid: SudokuGrid): SudokuGrid {
        return withContext(Dispatchers.IO) {
            try {
                val request = SolvePuzzleRequest(grid)
                val response = apiService.solvePuzzle(request)
                if (response.isSuccessful) {
                    response.body()?.solution ?: emptyArray()
                } else {
                    throw Exception("Failed to solve puzzle: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                throw Exception("Failed to solve puzzle: ${e.message}")
            }
        }
    }

    override suspend fun saveGameProgress(
        userId: Int,
        puzzleId: Int,
        currentGrid: SudokuGrid,
        isCompleted: Boolean,
        timeSpentSeconds: Int
    ): GameplayRecord {
        return withContext(Dispatchers.IO) {
            val accessToken = authRepository.getValidAccessToken()
                ?: throw Exception("No valid access token available")
            
            val request = SaveProgressRequest(
                userId = userId,
                puzzleId = puzzleId,
                currentGrid = currentGrid,
                isCompleted = isCompleted,
                timeSpentSeconds = timeSpentSeconds
            )
            
            val response = apiService.saveGameProgress("Bearer $accessToken", request)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response from server")
            } else {
                throw Exception("Failed to save progress: ${response.code()} ${response.message()}")
            }
        }
    }

    override suspend fun getUserStats(userId: Int): UserStats {
        return withContext(Dispatchers.IO) {
            val accessToken = authRepository.getValidAccessToken()
                ?: throw Exception("No valid access token available")
            
            val response = apiService.getUserStats("Bearer $accessToken", userId)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response from server")
            } else {
                throw Exception("Failed to get user stats: ${response.code()} ${response.message()}")
            }
        }
    }

    // Offline functionality
    override suspend fun getOfflinePuzzle(difficulty: SudokuDifficulty): SudokuPuzzle? {
        return withContext(Dispatchers.IO) {
            val entity = database.offlinePuzzleDao().getPuzzleByDifficulty(difficulty)
            entity?.let {
                SudokuPuzzle(
                    id = it.id,
                    grid = it.grid,
                    solution = it.solution,
                    difficulty = it.difficulty
                )
            }
        }
    }

    override suspend fun saveOfflinePuzzle(puzzle: SudokuPuzzle) {
        withContext(Dispatchers.IO) {
            val entity = OfflinePuzzleEntity(
                id = puzzle.id,
                grid = puzzle.grid,
                solution = puzzle.solution,
                difficulty = puzzle.difficulty
            )
            database.offlinePuzzleDao().insertPuzzle(entity)
        }
    }

    override suspend fun getOfflinePuzzles(difficulty: SudokuDifficulty): List<SudokuPuzzle> {
        return withContext(Dispatchers.IO) {
            val entities = database.offlinePuzzleDao().getPuzzlesByDifficulty(difficulty)
            entities.map { entity ->
                SudokuPuzzle(
                    id = entity.id,
                    grid = entity.grid,
                    solution = entity.solution,
                    difficulty = entity.difficulty
                )
            }
        }
    }

    override suspend fun saveLocalProgress(record: StoredGameRecord) {
        withContext(Dispatchers.IO) {
            val entity = GameProgressEntity(
                puzzleId = record.puzzleId,
                userId = record.userId,
                currentGrid = record.currentGrid,
                originalGrid = record.originalGrid,
                difficulty = record.difficulty,
                isCompleted = record.isCompleted,
                timeSpentSeconds = record.timeSpentSeconds,
                timestamp = record.timestamp
            )
            database.gameProgressDao().insertProgress(entity)
        }
    }

    override suspend fun getLocalProgress(): List<StoredGameRecord> {
        return withContext(Dispatchers.IO) {
            val entities = database.gameProgressDao().getAllProgress()
            entities.map { entity ->
                StoredGameRecord(
                    puzzleId = entity.puzzleId,
                    userId = entity.userId,
                    currentGrid = entity.currentGrid,
                    originalGrid = entity.originalGrid,
                    difficulty = entity.difficulty,
                    isCompleted = entity.isCompleted,
                    timeSpentSeconds = entity.timeSpentSeconds,
                    timestamp = entity.timestamp
                )
            }
        }
    }

    override suspend fun clearExpiredPuzzles() {
        withContext(Dispatchers.IO) {
            val weekAgo = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -7)
            }.time
            
            database.offlinePuzzleDao().deleteExpiredPuzzles(weekAgo)
            database.gameProgressDao().deleteOldProgress(weekAgo)
        }
    }

    private fun validateMoveLocally(grid: SudokuGrid, row: Int, col: Int, value: Int): Boolean {
        // Check row
        for (c in 0..8) {
            if (c != col && grid[row][c] == value) {
                return false
            }
        }
        
        // Check column
        for (r in 0..8) {
            if (r != row && grid[r][col] == value) {
                return false
            }
        }
        
        // Check 3x3 box
        val boxRow = (row / 3) * 3
        val boxCol = (col / 3) * 3
        
        for (r in boxRow until boxRow + 3) {
            for (c in boxCol until boxCol + 3) {
                if (r != row && c != col && grid[r][c] == value) {
                    return false
                }
            }
        }
        
        return true
    }
}