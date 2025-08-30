package com.sudokumaster.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sudokumaster.android.domain.repository.SudokuRepository
import com.sudokumaster.android.domain.repository.AuthRepository
import com.sudokumaster.android.domain.model.*
import com.sudokumaster.android.utils.NetworkMonitor
import com.sudokumaster.android.utils.PerformanceMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SudokuGameViewModel @Inject constructor(
    private val sudokuRepository: SudokuRepository,
    private val authRepository: AuthRepository,
    private val networkMonitor: NetworkMonitor,
    private val performanceMonitor: PerformanceMonitor
) : ViewModel() {

    // Game state
    private val _grid = MutableStateFlow(createEmptyGrid())
    val grid = _grid.asStateFlow()

    private val _originalGrid = MutableStateFlow(createEmptyGrid())
    val originalGrid = _originalGrid.asStateFlow()

    private val _selectedCell = MutableStateFlow<CellPosition?>(null)
    val selectedCell = _selectedCell.asStateFlow()

    private val _difficulty = MutableStateFlow(SudokuDifficulty.EASY)
    val difficulty = _difficulty.asStateFlow()

    private val _errors = MutableStateFlow<Map<CellPosition, Boolean>>(emptyMap())
    val errors = _errors.asStateFlow()

    private val _hintCell = MutableStateFlow<Triple<Int, Int, Int>?>(null)
    val hintCell = _hintCell.asStateFlow()

    // Game status
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isVictory = MutableStateFlow(false)
    val isVictory = _isVictory.asStateFlow()

    private val _showVictoryAlert = MutableStateFlow(false)
    val showVictoryAlert = _showVictoryAlert.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _timeSpentSeconds = MutableStateFlow(0)
    val timeSpentSeconds = _timeSpentSeconds.asStateFlow()

    private val _puzzleId = MutableStateFlow<Int?>(null)
    val puzzleId = _puzzleId.asStateFlow()

    // Offline mode
    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode = _isOfflineMode.asStateFlow()

    // Performance optimizations
    private var timerJob: Job? = null
    private val validationDebouncer = MutableSharedFlow<Triple<Int, Int, Int>>()
    
    // Cache for validation results
    private val validationCache = mutableMapOf<String, Boolean>()
    private val maxCacheSize = 100

    // Ad integration tracking
    private var gamesCompleted = 0
    private val adFrequency = 3 // Show ad every 3 completed games

    // Background dispatcher for heavy operations
    private val backgroundDispatcher = Dispatchers.IO.limitedParallelism(3)

    init {
        setupValidationDebouncer()
        loadTestPuzzle()
        
        // Monitor network connectivity
        viewModelScope.launch {
            networkMonitor.isConnected.collect { connected ->
                if (!connected && !_isOfflineMode.value) {
                    _isOfflineMode.value = true
                }
            }
        }
    }

    private fun createEmptyGrid(): SudokuGrid {
        return Array(9) { Array(9) { null } }
    }

    private fun setupValidationDebouncer() {
        viewModelScope.launch {
            validationDebouncer
                .debounce(200)
                .collect { (row, col, value) ->
                    performValidation(row, col, value)
                }
        }
    }

    fun newGame() {
        println("🎯 Starting new game with difficulty: ${_difficulty.value}, offline mode: ${_isOfflineMode.value}")
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                if (_isOfflineMode.value) {
                    println("🎯 Using offline mode")
                    loadOfflinePuzzle()
                } else {
                    println("🎯 Using online mode - calling API")
                    val puzzle = sudokuRepository.generatePuzzle(_difficulty.value)
                    updateGameState(puzzle)
                    
                    // Background download of additional puzzles
                    launch(backgroundDispatcher) {
                        downloadAdditionalPuzzlesInBackground()
                    }
                }
            } catch (error: Exception) {
                handleGameLoadError(error)
            }
        }
    }

    private suspend fun updateGameState(puzzle: SudokuPuzzle) {
        _grid.value = puzzle.grid
        _originalGrid.value = puzzle.grid
        _puzzleId.value = puzzle.id
        resetGameState()
        _isLoading.value = false
        println("🎯 Successfully loaded API puzzle with ${puzzle.grid.sumOf { row -> row.count { it != null } }} filled cells")
    }

    private suspend fun handleGameLoadError(error: Exception) {
        println("🎯 Error loading puzzle: $error")
        
        if (!_isOfflineMode.value) {
            println("🎯 API failed - switching to offline mode as fallback")
            _isOfflineMode.value = true
            _errorMessage.value = null
            loadOfflinePuzzle()
        } else {
            println("🎯 Both online and offline attempts failed")
            _errorMessage.value = "Unable to load puzzle. Please try again."
            _isLoading.value = false
        }
    }

    private suspend fun loadOfflinePuzzle() {
        val puzzle = sudokuRepository.getOfflinePuzzle(_difficulty.value)
        
        if (puzzle != null) {
            _grid.value = puzzle.grid
            _originalGrid.value = puzzle.grid
            _puzzleId.value = puzzle.id
            resetGameState()
            _isLoading.value = false
            println("✅ Successfully loaded offline puzzle with ${puzzle.grid.sumOf { row -> row.count { it != null } }} filled cells")
        } else {
            // Try to download a puzzle for this difficulty if network is available
            tryDownloadPuzzleForCurrentDifficulty()
        }
    }

    private suspend fun tryDownloadPuzzleForCurrentDifficulty() {
        // Only try to download if we're not in manual offline mode and have network
        if (!_isOfflineMode.value && networkMonitor.isConnected.value) {
            println("🔄 No offline puzzles for ${_difficulty.value.displayName}. Attempting to download...")
            
            try {
                val puzzle = sudokuRepository.generatePuzzle(_difficulty.value)
                
                // Store it for future use
                sudokuRepository.saveOfflinePuzzle(puzzle)
                
                // Use the downloaded puzzle immediately
                _grid.value = puzzle.grid
                _originalGrid.value = puzzle.grid
                _puzzleId.value = puzzle.id
                resetGameState()
                _isLoading.value = false
                _errorMessage.value = null
                println("✅ Downloaded and loaded puzzle for ${_difficulty.value.displayName}")
                
            } catch (error: Exception) {
                println("⚠️ Failed to download puzzle: ${error.message}")
                loadFallbackPuzzle()
            }
        } else {
            println("⚠️ Cannot download puzzles - offline mode or no network")
            loadFallbackPuzzle()
        }
    }

    private suspend fun downloadAdditionalPuzzlesInBackground() {
        if (!networkMonitor.isConnected.value) return
        
        println("📥 Background download: Adding puzzles for ${_difficulty.value.displayName} difficulty")
        
        // Download 2-3 additional puzzles for current difficulty
        repeat(3) { i ->
            try {
                val puzzle = sudokuRepository.generatePuzzle(_difficulty.value)
                sudokuRepository.saveOfflinePuzzle(puzzle)
                println("📥 Downloaded additional puzzle ${i + 1}/3 for ${_difficulty.value.displayName}")
            } catch (error: Exception) {
                println("⚠️ Failed to download additional puzzle ${i + 1}: $error")
                return@repeat
            }
        }
        println("✅ Added additional puzzles to offline storage for ${_difficulty.value.displayName}")
    }

    private suspend fun loadFallbackPuzzle() {
        println("Creating fallback puzzle for difficulty: ${_difficulty.value.displayName}")
        
        val fallbackPuzzle = withContext(backgroundDispatcher) {
            createFallbackPuzzle(_difficulty.value)
        }
        
        _grid.value = fallbackPuzzle.grid
        _originalGrid.value = fallbackPuzzle.grid
        _puzzleId.value = fallbackPuzzle.id
        resetGameState()
        _isLoading.value = false
        _errorMessage.value = null
        println("✅ Created fallback puzzle with ${fallbackPuzzle.grid.sumOf { row -> row.count { it != null } }} filled cells")
    }

    fun setSelectedCell(position: CellPosition?) {
        _selectedCell.value = position
    }

    fun setDifficulty(difficulty: SudokuDifficulty) {
        println("🎯 Setting difficulty to: ${difficulty.value}")
        _difficulty.value = difficulty
        _errorMessage.value = null
        newGame()
    }

    fun enterNumber(number: Int) {
        val selectedPos = _selectedCell.value ?: return
        
        // Don't allow changing original cells
        if (_originalGrid.value[selectedPos.row][selectedPos.col] != null) {
            println("Cannot modify original cell at (${selectedPos.row}, ${selectedPos.col})")
            return
        }
        
        println("Entering number $number at position (${selectedPos.row}, ${selectedPos.col})")
        
        val newGrid = _grid.value.map { it.clone() }.toTypedArray()
        newGrid[selectedPos.row][selectedPos.col] = number
        _grid.value = newGrid
        
        // Use debounced validation to improve performance
        viewModelScope.launch {
            validationDebouncer.emit(Triple(selectedPos.row, selectedPos.col, number))
        }
    }

    fun eraseNumber() {
        val selectedPos = _selectedCell.value ?: return
        
        // Don't allow erasing original cells
        if (_originalGrid.value[selectedPos.row][selectedPos.col] != null) return
        
        val newGrid = _grid.value.map { it.clone() }.toTypedArray()
        newGrid[selectedPos.row][selectedPos.col] = null
        _grid.value = newGrid
        
        // Remove any error for this cell
        val newErrors = _errors.value.toMutableMap()
        newErrors.remove(selectedPos)
        _errors.value = newErrors
    }

    fun redo() {
        // Simple redo functionality - restart the current puzzle
        val currentGrid = _originalGrid.value
        _grid.value = currentGrid.map { it.clone() }.toTypedArray()
        _errors.value = emptyMap()
        _selectedCell.value = null
        _hintCell.value = null
        
        // Reset timer
        _timeSpentSeconds.value = 0
        startTimer()
    }

    fun getHint() {
        val selectedPos = _selectedCell.value ?: return
        
        // Don't give hints for original cells or cells that already have a value
        if (_originalGrid.value[selectedPos.row][selectedPos.col] != null || 
            _grid.value[selectedPos.row][selectedPos.col] != null) return
        
        viewModelScope.launch {
            try {
                val solution = if (_isOfflineMode.value) {
                    solveGridLocally(_grid.value)
                } else {
                    sudokuRepository.solvePuzzle(_grid.value)
                }
                
                if (solution.isNotEmpty()) {
                    val hintValue = solution[selectedPos.row][selectedPos.col]
                    _hintCell.value = Triple(selectedPos.row, selectedPos.col, hintValue ?: 0)
                    
                    // Clear the hint after 3 seconds
                    delay(3000)
                    _hintCell.value = null
                }
            } catch (error: Exception) {
                _errorMessage.value = "Failed to generate hint: ${error.message}"
            }
        }
    }

    fun autoSolve() {
        viewModelScope.launch {
            try {
                val solution = if (_isOfflineMode.value) {
                    solveGridLocally(_grid.value)
                } else {
                    sudokuRepository.solvePuzzle(_grid.value)
                }
                
                if (solution.isNotEmpty()) {
                    _grid.value = solution
                    checkVictoryWithoutAnimation()
                } else {
                    _errorMessage.value = "Puzzle cannot be solved from current state"
                }
            } catch (error: Exception) {
                _errorMessage.value = "Failed to solve puzzle: ${error.message}"
            }
        }
    }

    fun clearUserInputs() {
        val newGrid = _originalGrid.value.map { it.clone() }.toTypedArray()
        _grid.value = newGrid
        _errors.value = emptyMap()
        validationCache.clear()
    }

    fun closeVictoryModal() {
        _showVictoryAlert.value = false
        gamesCompleted++
        
        // Show interstitial ad based on frequency
        if (gamesCompleted >= adFrequency && gamesCompleted % adFrequency == 0) {
            // TODO: Show interstitial ad
            println("📺 Would show interstitial ad after $gamesCompleted games completed")
        }
    }

    private fun resetGameState() {
        _selectedCell.value = null
        _errors.value = emptyMap()
        validationCache.clear()
        _hintCell.value = null
        _isVictory.value = false
        _showVictoryAlert.value = false
        _errorMessage.value = null
        _timeSpentSeconds.value = 0
        startTimer()
    }

    private suspend fun performValidation(row: Int, col: Int, value: Int) {
        val cacheKey = getCacheKey(_grid.value, row, col, value)
        
        // Check cache first
        val cachedResult = validationCache[cacheKey]
        if (cachedResult != null) {
            updateValidationResult(row, col, cachedResult)
            return
        }
        
        try {
            val isValid = if (_isOfflineMode.value) {
                validateLocalMove(row, col, value)
            } else {
                sudokuRepository.validateMove(_grid.value, row, col, value)
            }
            
            // Cache the result
            cacheValidationResult(cacheKey, isValid)
            updateValidationResult(row, col, isValid)
            
        } catch (error: Exception) {
            _errorMessage.value = "Failed to validate move: ${error.message}"
        }
    }

    private fun updateValidationResult(row: Int, col: Int, isValid: Boolean) {
        val position = CellPosition(row, col)
        val newErrors = _errors.value.toMutableMap()
        if (isValid) {
            newErrors.remove(position)
        } else {
            newErrors[position] = true
        }
        _errors.value = newErrors
        checkVictory()
    }

    private fun validateLocalMove(row: Int, col: Int, value: Int): Boolean {
        val currentGrid = _grid.value
        
        // Check row
        for (c in 0..8) {
            if (c != col && currentGrid[row][c] == value) {
                return false
            }
        }
        
        // Check column
        for (r in 0..8) {
            if (r != row && currentGrid[r][col] == value) {
                return false
            }
        }
        
        // Check 3x3 box
        val boxRow = (row / 3) * 3
        val boxCol = (col / 3) * 3
        
        for (r in boxRow until boxRow + 3) {
            for (c in boxCol until boxCol + 3) {
                if (r != row && c != col && currentGrid[r][c] == value) {
                    return false
                }
            }
        }
        
        return true
    }

    private fun checkVictory() {
        val currentGrid = _grid.value
        val currentErrors = _errors.value
        
        // Check if all cells are filled
        val allFilled = currentGrid.all { row -> row.all { it != null } }
        if (!allFilled) return
        
        // Check if there are no errors
        val hasErrors = currentErrors.values.any { it }
        if (hasErrors) return
        
        _isVictory.value = true
        _showVictoryAlert.value = true
        stopTimer()
        
        // Save game progress
        viewModelScope.launch {
            saveProgressAsync(isCompleted = true)
            performanceMonitor.recordCustomMetric("game_completion", 1.0)
        }
    }

    private fun checkVictoryWithoutAnimation() {
        val currentGrid = _grid.value
        val currentErrors = _errors.value
        
        // Check if all cells are filled and no errors
        val allFilled = currentGrid.all { row -> row.all { it != null } }
        val hasErrors = currentErrors.values.any { it }
        
        if (allFilled && !hasErrors) {
            _isVictory.value = true
            stopTimer()
            
            viewModelScope.launch {
                saveProgressAsync(isCompleted = true)
            }
        }
    }

    private fun getCacheKey(grid: SudokuGrid, row: Int, col: Int, value: Int): String {
        val gridHash = grid.joinToString("") { it.joinToString("") { cell -> cell?.toString() ?: "null" } }
        return "${gridHash}_${row}_${col}_$value"
    }

    private fun cacheValidationResult(key: String, result: Boolean) {
        if (validationCache.size >= maxCacheSize) {
            // Remove oldest entries (simple approach - remove first half)
            val keysToRemove = validationCache.keys.take(maxCacheSize / 2)
            keysToRemove.forEach { validationCache.remove(it) }
        }
        validationCache[key] = result
    }

    private fun startTimer() {
        stopTimer()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _timeSpentSeconds.value = _timeSpentSeconds.value + 1
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private suspend fun saveProgressAsync(isCompleted: Boolean) {
        val currentPuzzleId = _puzzleId.value ?: return
        println("🔍 Saving progress for puzzle: $currentPuzzleId, completed: $isCompleted")
        
        try {
            if (_isOfflineMode.value) {
                saveLocalProgress(isCompleted)
            } else {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    sudokuRepository.saveGameProgress(
                        userId = user.id,
                        puzzleId = currentPuzzleId,
                        currentGrid = _grid.value,
                        isCompleted = isCompleted,
                        timeSpentSeconds = _timeSpentSeconds.value
                    )
                    println("✅ Game progress saved")
                }
            }
        } catch (error: Exception) {
            _errorMessage.value = "Failed to save progress: ${error.message}"
        }
    }

    private suspend fun saveLocalProgress(isCompleted: Boolean) {
        val currentPuzzleId = _puzzleId.value ?: return
        val user = authRepository.getCurrentUser()
        
        val record = StoredGameRecord(
            puzzleId = currentPuzzleId,
            userId = user?.id,
            currentGrid = _grid.value,
            originalGrid = _originalGrid.value,
            difficulty = _difficulty.value,
            isCompleted = isCompleted,
            timeSpentSeconds = _timeSpentSeconds.value,
            timestamp = java.util.Date()
        )
        
        sudokuRepository.saveLocalProgress(record)
    }

    fun setOfflineMode(isOffline: Boolean) {
        _isOfflineMode.value = isOffline
    }

    private fun loadTestPuzzle() {
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            val testPuzzle = withContext(backgroundDispatcher) {
                createFallbackPuzzle(_difficulty.value)
            }
            
            _grid.value = testPuzzle.grid
            _originalGrid.value = testPuzzle.grid
            _puzzleId.value = testPuzzle.id
            resetGameState()
            _isLoading.value = false
            println("Loaded test puzzle with ${testPuzzle.grid.sumOf { row -> row.count { it != null } }} filled cells")
        }
    }

    // Sudoku solving and generation logic
    private fun createFallbackPuzzle(difficulty: SudokuDifficulty): SudokuPuzzle {
        println("🎯 Creating fallback puzzle for difficulty: ${difficulty.value}")
        
        // Pre-computed solution for better performance
        val solution: SudokuGrid = arrayOf(
            arrayOf(5, 3, 4, 6, 7, 8, 9, 1, 2),
            arrayOf(6, 7, 2, 1, 9, 5, 3, 4, 8),
            arrayOf(1, 9, 8, 3, 4, 2, 5, 6, 7),
            arrayOf(8, 5, 9, 7, 6, 1, 4, 2, 3),
            arrayOf(4, 2, 6, 8, 5, 3, 7, 9, 1),
            arrayOf(7, 1, 3, 9, 2, 4, 8, 5, 6),
            arrayOf(9, 6, 1, 5, 3, 7, 2, 8, 4),
            arrayOf(2, 8, 7, 4, 1, 9, 6, 3, 5),
            arrayOf(3, 4, 5, 2, 8, 6, 1, 7, 9)
        )
        
        val grid = Array(9) { Array<Int?>(9) { null } }
        
        // Get positions for difficulty
        val positions = getPositionsForDifficulty(difficulty)
        
        // Fill the grid efficiently
        for ((row, col) in positions) {
            grid[row][col] = solution[row][col]
        }
        
        val filledCells = positions.size
        println("🎯 Created fallback puzzle with $filledCells filled cells for ${difficulty.value}")
        
        return SudokuPuzzle(id = -1, grid = grid, solution = solution, difficulty = difficulty)
    }

    private fun getPositionsForDifficulty(difficulty: SudokuDifficulty): List<Pair<Int, Int>> {
        return when (difficulty) {
            SudokuDifficulty.EASY -> listOf(
                0 to 0, 0 to 1, 0 to 2, 0 to 4, 0 to 6, 0 to 7,
                1 to 0, 1 to 2, 1 to 3, 1 to 4, 1 to 5, 1 to 7, 1 to 8,
                2 to 0, 2 to 1, 2 to 3, 2 to 5, 2 to 7, 2 to 8,
                3 to 0, 3 to 2, 3 to 4, 3 to 6, 3 to 8,
                4 to 0, 4 to 2, 4 to 3, 4 to 5, 4 to 6, 4 to 8,
                5 to 0, 5 to 2, 5 to 4, 5 to 6, 5 to 8,
                6 to 0, 6 to 1, 6 to 3, 6 to 5, 6 to 7, 6 to 8,
                7 to 0, 7 to 2, 7 to 3, 7 to 4, 7 to 5, 7 to 7, 7 to 8,
                8 to 0, 8 to 1, 8 to 2, 8 to 4, 8 to 6, 8 to 7, 8 to 8
            )
            
            SudokuDifficulty.MEDIUM -> listOf(
                0 to 0, 0 to 2, 0 to 4, 0 to 7,
                1 to 0, 1 to 3, 1 to 5, 1 to 8,
                2 to 1, 2 to 3, 2 to 5, 2 to 7,
                3 to 0, 3 to 4, 3 to 8,
                4 to 2, 4 to 3, 4 to 5, 4 to 6,
                5 to 0, 5 to 4, 5 to 8,
                6 to 1, 6 to 3, 6 to 5, 6 to 7,
                7 to 0, 7 to 3, 7 to 5, 7 to 8,
                8 to 1, 8 to 4, 8 to 6, 8 to 8,
                2 to 0, 3 to 2, 4 to 0, 4 to 8, 5 to 2, 6 to 0, 7 to 1
            )
            
            SudokuDifficulty.HARD -> listOf(
                0 to 0, 0 to 4, 0 to 8,
                1 to 2, 1 to 6,
                2 to 1, 2 to 7,
                3 to 0, 3 to 8,
                4 to 3, 4 to 5,
                5 to 0, 5 to 8,
                6 to 1, 6 to 7,
                7 to 2, 7 to 6,
                8 to 0, 8 to 4, 8 to 8,
                1 to 0, 3 to 4, 4 to 1, 4 to 7, 5 to 4, 7 to 8
            )
        }
    }

    private suspend fun solveGridLocally(grid: SudokuGrid): SudokuGrid {
        return withContext(backgroundDispatcher) {
            // Implementation of backtracking solver
            val gridCopy = grid.map { it.clone() }.toTypedArray()
            if (solveBacktrack(gridCopy)) gridCopy else emptyArray()
        }
    }

    private fun solveBacktrack(grid: SudokuGrid): Boolean {
        for (row in 0..8) {
            for (col in 0..8) {
                if (grid[row][col] == null) {
                    for (num in 1..9) {
                        if (isValidPlacement(grid, row, col, num)) {
                            grid[row][col] = num
                            
                            if (solveBacktrack(grid)) {
                                return true
                            }
                            
                            grid[row][col] = null
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun isValidPlacement(grid: SudokuGrid, row: Int, col: Int, num: Int): Boolean {
        // Check row
        for (c in 0..8) {
            if (grid[row][c] == num) return false
        }
        
        // Check column
        for (r in 0..8) {
            if (grid[r][col] == num) return false
        }
        
        // Check 3x3 box
        val boxRow = (row / 3) * 3
        val boxCol = (col / 3) * 3
        
        for (r in boxRow until boxRow + 3) {
            for (c in boxCol until boxCol + 3) {
                if (grid[r][c] == num) return false
            }
        }
        
        return true
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}