package com.sudokumaster.android

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SudokuApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        println("🚀 Sudoku Master Android app started")
    }
    
    override fun onTerminate() {
        super.onTerminate()
        println("🔚 Sudoku Master Android app terminated")
    }
    
    override fun onLowMemory() {
        super.onLowMemory()
        println("⚠️ Low memory warning received")
        // The AdManager and PerformanceMonitor will handle memory cleanup via their injected instances
    }
}