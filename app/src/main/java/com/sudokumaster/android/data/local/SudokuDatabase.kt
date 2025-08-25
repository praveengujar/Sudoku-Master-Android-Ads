package com.sudokumaster.android.data.local

import androidx.room.*
import androidx.room.TypeConverters
import com.sudokumaster.android.domain.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

// Type converters for complex types
class Converters {
    @TypeConverter
    fun fromSudokuGrid(grid: SudokuGrid): String {
        return Gson().toJson(grid)
    }

    @TypeConverter
    fun toSudokuGrid(gridString: String): SudokuGrid {
        val type = object : TypeToken<Array<Array<Int?>>>() {}.type
        return Gson().fromJson(gridString, type)
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun fromDifficulty(difficulty: SudokuDifficulty): String {
        return difficulty.value
    }

    @TypeConverter
    fun toDifficulty(value: String): SudokuDifficulty {
        return SudokuDifficulty.fromString(value)
    }
}

// Entity for offline puzzles
@Entity(tableName = "offline_puzzles")
data class OfflinePuzzleEntity(
    @PrimaryKey val id: Int,
    val grid: SudokuGrid,
    val solution: SudokuGrid,
    val difficulty: SudokuDifficulty,
    val createdAt: Date = Date()
)

// Entity for local game progress
@Entity(tableName = "game_progress")
data class GameProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val puzzleId: Int,
    val userId: Int?,
    val currentGrid: SudokuGrid,
    val originalGrid: SudokuGrid,
    val difficulty: SudokuDifficulty,
    val isCompleted: Boolean,
    val timeSpentSeconds: Int,
    val timestamp: Date = Date()
)

// DAO for offline puzzles
@Dao
interface OfflinePuzzleDao {
    @Query("SELECT * FROM offline_puzzles WHERE difficulty = :difficulty ORDER BY createdAt DESC LIMIT 1")
    suspend fun getPuzzleByDifficulty(difficulty: SudokuDifficulty): OfflinePuzzleEntity?

    @Query("SELECT * FROM offline_puzzles WHERE difficulty = :difficulty ORDER BY createdAt DESC")
    suspend fun getPuzzlesByDifficulty(difficulty: SudokuDifficulty): List<OfflinePuzzleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPuzzle(puzzle: OfflinePuzzleEntity)

    @Query("DELETE FROM offline_puzzles WHERE createdAt < :expireTime")
    suspend fun deleteExpiredPuzzles(expireTime: Date)

    @Query("DELETE FROM offline_puzzles")
    suspend fun deleteAllPuzzles()

    @Query("SELECT COUNT(*) FROM offline_puzzles WHERE difficulty = :difficulty")
    suspend fun getPuzzleCount(difficulty: SudokuDifficulty): Int
}

// DAO for game progress
@Dao
interface GameProgressDao {
    @Query("SELECT * FROM game_progress ORDER BY timestamp DESC")
    suspend fun getAllProgress(): List<GameProgressEntity>

    @Query("SELECT * FROM game_progress WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getProgressByUser(userId: Int): List<GameProgressEntity>

    @Query("SELECT * FROM game_progress WHERE puzzleId = :puzzleId AND userId = :userId LIMIT 1")
    suspend fun getProgressByPuzzleAndUser(puzzleId: Int, userId: Int?): GameProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: GameProgressEntity)

    @Query("DELETE FROM game_progress WHERE timestamp < :expireTime")
    suspend fun deleteOldProgress(expireTime: Date)

    @Query("DELETE FROM game_progress")
    suspend fun deleteAllProgress()
}

// Main database
@Database(
    entities = [OfflinePuzzleEntity::class, GameProgressEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SudokuDatabase : RoomDatabase() {
    abstract fun offlinePuzzleDao(): OfflinePuzzleDao
    abstract fun gameProgressDao(): GameProgressDao
}