package com.sudokumaster.android.data.remote

import com.sudokumaster.android.domain.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    @GET(".")
    suspend fun healthCheck(): Response<String>
    
    // Authentication endpoints
    @POST("users/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>
    
    @POST("users/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>
    
    @POST("users/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<RefreshTokenResponse>
    
    @POST("users/logout")
    suspend fun logout(
        @Header("Authorization") bearerToken: String,
        @Body request: LogoutRequest
    ): Response<Unit>
    
    @GET("users/me")
    suspend fun getCurrentUser(
        @Header("Authorization") bearerToken: String
    ): Response<User>
    
    @DELETE("users/{username}")
    suspend fun deleteUser(
        @Header("Authorization") bearerToken: String,
        @Path("username") username: String
    ): Response<Unit>
    
    // Sudoku endpoints
    @GET("sudoku/generate")
    suspend fun generatePuzzle(
        @Query("difficulty") difficulty: String
    ): Response<SudokuPuzzle>
    
    @POST("sudoku/validate")
    suspend fun validateMove(
        @Body request: ValidateMoveRequest
    ): Response<ValidateMoveResponse>
    
    @POST("sudoku/solve")
    suspend fun solvePuzzle(
        @Body request: SolvePuzzleRequest
    ): Response<SolvePuzzleResponse>
    
    @POST("sudoku/save-progress")
    suspend fun saveGameProgress(
        @Header("Authorization") bearerToken: String,
        @Body request: SaveProgressRequest
    ): Response<GameplayRecord>
    
    @GET("sudoku/user-stats/{userId}")
    suspend fun getUserStats(
        @Header("Authorization") bearerToken: String,
        @Path("userId") userId: Int
    ): Response<UserStats>
}

// Request/Response models
data class RegisterRequest(
    val username: String,
    val password: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class ValidateMoveRequest(
    val grid: Array<Array<Int?>>,
    val row: Int,
    val col: Int,
    val value: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ValidateMoveRequest

        if (!grid.contentDeepEquals(other.grid)) return false
        if (row != other.row) return false
        if (col != other.col) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = grid.contentDeepHashCode()
        result = 31 * result + row
        result = 31 * result + col
        result = 31 * result + value
        return result
    }
}

data class ValidateMoveResponse(
    val isValid: Boolean
)

data class SolvePuzzleRequest(
    val grid: Array<Array<Int?>>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SolvePuzzleRequest

        if (!grid.contentDeepEquals(other.grid)) return false

        return true
    }

    override fun hashCode(): Int {
        return grid.contentDeepHashCode()
    }
}

data class SolvePuzzleResponse(
    val solution: Array<Array<Int?>>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SolvePuzzleResponse

        if (!solution.contentDeepEquals(other.solution)) return false

        return true
    }

    override fun hashCode(): Int {
        return solution.contentDeepHashCode()
    }
}

data class SaveProgressRequest(
    val userId: Int,
    val puzzleId: Int,
    val currentGrid: Array<Array<Int?>>,
    val isCompleted: Boolean,
    val timeSpentSeconds: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SaveProgressRequest

        if (userId != other.userId) return false
        if (puzzleId != other.puzzleId) return false
        if (!currentGrid.contentDeepEquals(other.currentGrid)) return false
        if (isCompleted != other.isCompleted) return false
        if (timeSpentSeconds != other.timeSpentSeconds) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId
        result = 31 * result + puzzleId
        result = 31 * result + currentGrid.contentDeepHashCode()
        result = 31 * result + isCompleted.hashCode()
        result = 31 * result + timeSpentSeconds
        return result
    }
}