package com.sudokumaster.android.utils

import android.content.Context
import android.os.Debug
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PerformanceMonitor @Inject constructor(
    private val context: Context
) {
    
    private val activeOperations = ConcurrentHashMap<String, Long>()
    private val completedOperations = ConcurrentHashMap<String, OperationMetric>()
    private val customMetrics = ConcurrentHashMap<String, Double>()
    
    private val _memoryUsage = MutableStateFlow(0L)
    val memoryUsage: StateFlow<Long> = _memoryUsage.asStateFlow()
    
    private val _isMonitoring = MutableStateFlow(false)
    val isMonitoring: StateFlow<Boolean> = _isMonitoring.asStateFlow()
    
    private var monitoringJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    data class OperationMetric(
        val operationName: String,
        val startTime: Long,
        val endTime: Long,
        val duration: Long,
        val memoryBefore: Long,
        val memoryAfter: Long
    ) {
        val memoryDelta: Long get() = memoryAfter - memoryBefore
    }
    
    init {
        startPerformanceMonitoring()
    }
    
    private fun startPerformanceMonitoring() {
        if (_isMonitoring.value) return
        
        _isMonitoring.value = true
        monitoringJob = scope.launch {
            while (isActive) {
                try {
                    updateMemoryUsage()
                    delay(5000) // Update every 5 seconds
                } catch (e: Exception) {
                    println("⚠️ Performance monitoring error: ${e.message}")
                }
            }
        }
        
        println("✅ Performance monitoring started")
    }
    
    fun stopPerformanceMonitoring() {
        _isMonitoring.value = false
        monitoringJob?.cancel()
        monitoringJob = null
        println("🔌 Performance monitoring stopped")
    }
    
    private fun updateMemoryUsage() {
        val memInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(memInfo)
        _memoryUsage.value = memInfo.totalPss.toLong() * 1024 // Convert KB to bytes
    }
    
    fun startOperation(operationName: String) {
        val startTime = System.currentTimeMillis()
        activeOperations[operationName] = startTime
        println("⏱️ Started operation: $operationName")
    }
    
    fun endOperation(operationName: String) {
        val endTime = System.currentTimeMillis()
        val startTime = activeOperations.remove(operationName)
        
        if (startTime != null) {
            val duration = endTime - startTime
            val memoryBefore = _memoryUsage.value
            updateMemoryUsage()
            val memoryAfter = _memoryUsage.value
            
            val metric = OperationMetric(
                operationName = operationName,
                startTime = startTime,
                endTime = endTime,
                duration = duration,
                memoryBefore = memoryBefore,
                memoryAfter = memoryAfter
            )
            
            completedOperations[operationName] = metric
            
            println("✅ Completed operation: $operationName (${duration}ms, memory delta: ${metric.memoryDelta} bytes)")
            
            // Log slow operations
            if (duration > 1000) {
                println("🐌 Slow operation detected: $operationName took ${duration}ms")
            }
            
            // Log high memory usage operations
            if (metric.memoryDelta > 50 * 1024 * 1024) { // 50MB
                println("🧠 High memory usage operation: $operationName used ${metric.memoryDelta / (1024 * 1024)}MB")
            }
        } else {
            println("⚠️ End operation called without matching start: $operationName")
        }
    }
    
    fun recordCustomMetric(name: String, value: Double) {
        customMetrics[name] = value
        println("📊 Custom metric recorded: $name = $value")
    }
    
    fun getOperationMetric(operationName: String): OperationMetric? {
        return completedOperations[operationName]
    }
    
    fun getAllOperationMetrics(): Map<String, OperationMetric> {
        return completedOperations.toMap()
    }
    
    fun getCustomMetric(name: String): Double? {
        return customMetrics[name]
    }
    
    fun getAllCustomMetrics(): Map<String, Double> {
        return customMetrics.toMap()
    }
    
    fun getCurrentMemoryUsageFormatted(): String {
        val memoryMB = _memoryUsage.value / (1024 * 1024)
        return "${memoryMB}MB"
    }
    
    fun getActiveOperations(): Set<String> {
        return activeOperations.keys.toSet()
    }
    
    fun clearMetrics() {
        completedOperations.clear()
        customMetrics.clear()
        println("🧹 Performance metrics cleared")
    }
    
    fun generatePerformanceReport(): String {
        val report = StringBuilder()
        
        report.appendLine("📊 Performance Report")
        report.appendLine("==================")
        report.appendLine()
        
        // Memory Usage
        report.appendLine("Memory Usage: ${getCurrentMemoryUsageFormatted()}")
        report.appendLine()
        
        // Active Operations
        val activeOps = getActiveOperations()
        if (activeOps.isNotEmpty()) {
            report.appendLine("Active Operations:")
            activeOps.forEach { operation ->
                val startTime = activeOperations[operation]
                val duration = if (startTime != null) System.currentTimeMillis() - startTime else 0
                report.appendLine("  - $operation (running for ${duration}ms)")
            }
            report.appendLine()
        }
        
        // Completed Operations
        if (completedOperations.isNotEmpty()) {
            report.appendLine("Recent Operations:")
            completedOperations.values.sortedByDescending { it.endTime }.take(10).forEach { metric ->
                report.appendLine("  - ${metric.operationName}: ${metric.duration}ms, memory: ${metric.memoryDelta / 1024}KB")
            }
            report.appendLine()
        }
        
        // Custom Metrics
        if (customMetrics.isNotEmpty()) {
            report.appendLine("Custom Metrics:")
            customMetrics.toList().sortedByDescending { it.second }.take(10).forEach { (name, value) ->
                report.appendLine("  - $name: $value")
            }
            report.appendLine()
        }
        
        // Performance Warnings
        val slowOperations = completedOperations.values.filter { it.duration > 1000 }
        if (slowOperations.isNotEmpty()) {
            report.appendLine("⚠️ Slow Operations:")
            slowOperations.sortedByDescending { it.duration }.take(5).forEach { metric ->
                report.appendLine("  - ${metric.operationName}: ${metric.duration}ms")
            }
            report.appendLine()
        }
        
        val memoryHungryOperations = completedOperations.values.filter { it.memoryDelta > 10 * 1024 * 1024 }
        if (memoryHungryOperations.isNotEmpty()) {
            report.appendLine("🧠 Memory Intensive Operations:")
            memoryHungryOperations.sortedByDescending { it.memoryDelta }.take(5).forEach { metric ->
                report.appendLine("  - ${metric.operationName}: ${metric.memoryDelta / (1024 * 1024)}MB")
            }
        }
        
        return report.toString()
    }
    
    fun onMemoryWarning() {
        println("⚠️ Memory warning received - clearing old performance metrics")
        
        // Keep only the most recent 50 operation metrics
        if (completedOperations.size > 50) {
            val recentMetrics = completedOperations.values
                .sortedByDescending { it.endTime }
                .take(50)
                .associateBy { it.operationName }
            
            completedOperations.clear()
            completedOperations.putAll(recentMetrics)
        }
        
        // Keep only the most recent 25 custom metrics
        if (customMetrics.size > 25) {
            val recentCustomMetrics = customMetrics.toList()
                .takeLast(25)
                .toMap()
            
            customMetrics.clear()
            customMetrics.putAll(recentCustomMetrics)
        }
        
        // Force garbage collection
        System.gc()
        
        println("✅ Performance monitor memory cleanup completed")
    }
    
    fun cleanup() {
        stopPerformanceMonitoring()
        scope.cancel()
        activeOperations.clear()
        completedOperations.clear()
        customMetrics.clear()
        println("🧹 Performance monitor cleaned up")
    }
}