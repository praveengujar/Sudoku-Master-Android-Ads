package com.sudokumaster.android.utils

import android.app.Activity
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Temporary stub for AdManager to allow the app to build without Meta Audience Network
 * This can be used until the dependency conflict is resolved
 */
@Singleton
class AdManagerStub @Inject constructor(
    private val context: Context,
    private val performanceMonitor: PerformanceMonitor
) {
    
    // MARK: - Published Properties
    private val _isMetaInitialized = MutableStateFlow(true) // Always true for stub
    val isMetaInitialized: StateFlow<Boolean> = _isMetaInitialized.asStateFlow()
    
    private val _adLoadingState = MutableStateFlow(AdLoadingState.IDLE)
    val adLoadingState: StateFlow<AdLoadingState> = _adLoadingState.asStateFlow()
    
    private val _lastAdShownTime = MutableStateFlow<Date?>(null)
    val lastAdShownTime: StateFlow<Date?> = _lastAdShownTime.asStateFlow()

    enum class AdLoadingState {
        IDLE, LOADING, LOADED, FAILED, SHOWING
    }
    
    enum class AdType {
        BANNER, INTERSTITIAL, REWARDED
    }
    
    data class CachedAd(
        val ad: Any,
        val timestamp: Long,
        val adType: AdType
    )
    
    data class AdMetrics(
        val loadTime: Long = 0,
        val showCount: Int = 0,
        val clickCount: Int = 0,
        val errorCount: Int = 0,
        val revenue: Double = 0.0
    )

    init {
        println("✅ AdManagerStub initialized (no ads will be shown)")
    }

    // MARK: - Banner Ads
    
    fun createBannerAd(): Any? {
        println("📱 AdManagerStub: Banner ad creation skipped")
        return null
    }
    
    fun loadBannerAd() {
        println("📱 AdManagerStub: Banner ad loading skipped")
    }

    // MARK: - Interstitial Ads
    
    fun preloadInterstitialAd() {
        println("📱 AdManagerStub: Interstitial ad preload skipped")
    }
    
    fun showInterstitialAd(activity: Activity? = null) {
        println("📱 AdManagerStub: Interstitial ad show skipped")
        _lastAdShownTime.value = Date()
    }

    // MARK: - Rewarded Ads
    
    fun preloadRewardedAd() {
        println("📱 AdManagerStub: Rewarded ad preload skipped")
    }
    
    fun showRewardedAd(onRewardEarned: (() -> Unit)? = null): Boolean {
        println("📱 AdManagerStub: Rewarded ad show skipped - granting reward anyway")
        onRewardEarned?.invoke() // Always grant reward in stub mode
        return true
    }

    // MARK: - Public API
    
    fun getAdMetrics(adType: AdType): AdMetrics? {
        return AdMetrics() // Return empty metrics
    }
    
    fun getAllAdMetrics(): Map<AdType, AdMetrics> {
        return emptyMap()
    }
    
    fun isAdReady(adType: AdType): Boolean {
        return true // Always ready in stub mode
    }
    
    fun cleanupResources() {
        println("🧹 AdManagerStub: Cleanup skipped")
    }
    
    fun onMemoryWarning() {
        println("⚠️ AdManagerStub: Memory warning handled")
    }
}