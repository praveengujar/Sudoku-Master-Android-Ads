package com.sudokumaster.android.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitor @Inject constructor(
    private val context: Context
) {
    
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    private val _isConnected = MutableStateFlow(checkCurrentConnectivity())
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    
    private val _connectionType = MutableStateFlow(getCurrentConnectionType())
    val connectionType: StateFlow<ConnectionType> = _connectionType.asStateFlow()
    
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            println("🌐 Network available: $network")
            _isConnected.value = true
            _connectionType.value = getCurrentConnectionType()
        }
        
        override fun onLost(network: Network) {
            println("🚫 Network lost: $network")
            _isConnected.value = checkCurrentConnectivity()
            _connectionType.value = getCurrentConnectionType()
        }
        
        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            println("📶 Network capabilities changed: $network")
            _connectionType.value = getConnectionType(networkCapabilities)
        }
    }
    
    enum class ConnectionType {
        NONE, WIFI, CELLULAR, ETHERNET, VPN, OTHER
    }
    
    init {
        startMonitoring()
    }
    
    private fun startMonitoring() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()
        
        try {
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
            println("✅ Network monitoring started")
        } catch (e: Exception) {
            println("⚠️ Failed to start network monitoring: ${e.message}")
        }
    }
    
    private fun checkCurrentConnectivity(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    
    private fun getCurrentConnectionType(): ConnectionType {
        val activeNetwork = connectivityManager.activeNetwork ?: return ConnectionType.NONE
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return ConnectionType.NONE
        
        return getConnectionType(networkCapabilities)
    }
    
    private fun getConnectionType(networkCapabilities: NetworkCapabilities): ConnectionType {
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionType.WIFI
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectionType.CELLULAR
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ConnectionType.ETHERNET
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> ConnectionType.VPN
            else -> ConnectionType.OTHER
        }
    }
    
    fun stopMonitoring() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            println("🔌 Network monitoring stopped")
        } catch (e: Exception) {
            println("⚠️ Failed to stop network monitoring: ${e.message}")
        }
    }
    
    fun isWiFiConnected(): Boolean {
        return _connectionType.value == ConnectionType.WIFI
    }
    
    fun isCellularConnected(): Boolean {
        return _connectionType.value == ConnectionType.CELLULAR
    }
    
    fun getNetworkInfo(): String {
        return "Connected: ${_isConnected.value}, Type: ${_connectionType.value}"
    }
}