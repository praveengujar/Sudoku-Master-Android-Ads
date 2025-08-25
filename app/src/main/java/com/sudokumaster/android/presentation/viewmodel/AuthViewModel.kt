package com.sudokumaster.android.presentation.viewmodel

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sudokumaster.android.domain.repository.AuthRepository
import com.sudokumaster.android.domain.model.User
import com.sudokumaster.android.utils.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    private val _isGuestMode = MutableStateFlow(false)
    val isGuestMode = _isGuestMode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _biometricEnabled = MutableStateFlow(false)
    val biometricEnabled = _biometricEnabled.asStateFlow()

    private val _biometricType = MutableStateFlow<BiometricType>(BiometricType.NONE)
    val biometricType = _biometricType.asStateFlow()

    // Prevent multiple simultaneous authentication attempts
    private var isAuthenticating = false
    private var lastAuthAttempt: Long = 0

    val isLoggedInOrGuest: Boolean
        get() = _isAuthenticated.value || _isGuestMode.value

    val biometricDisplayName: String
        get() = when (_biometricType.value) {
            BiometricType.FACE -> "Face unlock"
            BiometricType.FINGERPRINT -> "Fingerprint"
            BiometricType.NONE -> "Biometric"
        }

    val isBiometricAvailable: Boolean
        get() = authRepository.isBiometricAvailable()

    val hasStoredCredentials: Boolean
        get() = authRepository.hasStoredTokens()

    enum class BiometricType {
        NONE, FINGERPRINT, FACE
    }

    init {
        setupBiometrics()
        checkExistingUser()
    }

    private fun setupBiometrics() {
        _biometricType.value = when {
            authRepository.isBiometricAvailable() -> BiometricType.FINGERPRINT // Simplified for Android
            else -> BiometricType.NONE
        }
        
        viewModelScope.launch {
            _biometricEnabled.value = authRepository.isBiometricEnabled()
        }
    }

    fun checkExistingUser() {
        println("🔍 AuthViewModel.checkExistingUser() called")
        viewModelScope.launch {
            performSingleAuthenticationFlow()
        }
    }

    private suspend fun performSingleAuthenticationFlow() {
        // Prevent multiple simultaneous authentication attempts
        if (isAuthenticating) {
            println("⚠️ Authentication already in progress - skipping")
            return
        }

        // Rate limiting: prevent authentication attempts within 2 seconds of each other
        val now = System.currentTimeMillis()
        if (now - lastAuthAttempt < 2000) {
            println("⚠️ Authentication rate limited - too soon after last attempt")
            return
        }

        lastAuthAttempt = now
        isAuthenticating = true

        try {
            _isLoading.value = true
            _error.value = null

            // Check if we have stored JWT tokens first (no biometric prompt)
            if (!authRepository.hasStoredTokens()) {
                println("⚠️ No stored JWT tokens found - user needs to login manually")
                return
            }

            println("✅ Found stored JWT tokens")

            // Check if biometric is required
            val biometricRequired = authRepository.isBiometricEnabled()

            if (biometricRequired) {
                println("⚠️ Biometric authentication required - skipping auto-login")
                println("ℹ️ User can use 'Sign in with $biometricDisplayName' button to authenticate")
                return
            }

            // Try to authenticate with stored JWT tokens
            performJWTAutoLogin()

        } catch (error: Exception) {
            println("⚠️ Error in JWT authentication flow: ${error.message}")
            authRepository.clearAuthTokens()
        } finally {
            _isLoading.value = false
            isAuthenticating = false
        }
    }

    private suspend fun performJWTAutoLogin() {
        try {
            println("🔄 Attempting JWT auto-login with stored tokens")

            // Try to get current user using stored JWT tokens
            val user = authRepository.getCurrentUser()

            if (user != null) {
                // Successfully authenticated with JWT tokens
                _currentUser.value = user
                _isAuthenticated.value = true
                _error.value = null

                println("✅ JWT auto-login successful for user: ${user.username}")
            } else {
                throw Exception("Failed to get current user")
            }

        } catch (error: Exception) {
            println("⚠️ JWT auto-login failed: ${error.message}")
            handleJWTAutoLoginError(error)
        }
    }

    private suspend fun handleJWTAutoLoginError(error: Exception) {
        when {
            error.message?.contains("401") == true || 
            error.message?.contains("authentication") == true -> {
                // JWT tokens are invalid or expired beyond refresh
                println("🔐 JWT tokens invalid - clearing stored tokens")
                authRepository.clearAuthTokens()
                authRepository.setBiometricEnabled(false)
                _biometricEnabled.value = false
                _error.value = "Your saved login has expired. Please sign in again."
            }
            
            error.message?.contains("network") == true || 
            error.message?.contains("server") == true -> {
                // Network/server issues - keep tokens
                println("ℹ️ Keeping stored tokens - error may be temporary: $error")
                _error.value = "Network issue. Your login will be restored when connection improves."
            }
            
            else -> {
                // Other errors
                println("ℹ️ Non-API error during JWT auto-login: $error")
                _error.value = "Unable to sign in automatically. Please try manual login."
            }
        }

        _currentUser.value = null
        _isAuthenticated.value = false
    }

    suspend fun login(username: String, password: String, enableBiometric: Boolean = false) {
        // Prevent multiple simultaneous login attempts
        if (isAuthenticating) {
            println("⚠️ Login already in progress - skipping")
            return
        }

        isAuthenticating = true

        try {
            _isLoading.value = true

            val user = authRepository.login(username, password, enableBiometric)

            _currentUser.value = user
            _isAuthenticated.value = true
            _biometricEnabled.value = enableBiometric
            _error.value = null

            println("✅ JWT login successful with biometric: $enableBiometric")

        } catch (error: Exception) {
            println("🔍 Login Error Details: $error")
            _error.value = when {
                error.message?.contains("connect") == true || 
                error.message?.contains("server") == true -> 
                    "Network issue. Try 'Continue as Guest' to play offline, or check your internet connection."
                
                error.message?.contains("credentials") == true || 
                error.message?.contains("401") == true -> 
                    "Invalid username or password. Please try again."
                
                else -> "Login failed: ${error.message}"
            }
            _isAuthenticated.value = false
        } finally {
            _isLoading.value = false
            isAuthenticating = false
        }
    }

    suspend fun loginWithBiometric(activity: FragmentActivity) {
        if (!authRepository.isBiometricAvailable()) {
            _error.value = "Biometric authentication is not available on this device"
            return
        }

        // Prevent multiple simultaneous biometric login attempts
        if (isAuthenticating) {
            println("⚠️ Biometric login already in progress - skipping")
            return
        }

        isAuthenticating = true

        try {
            _isLoading.value = true

            val user = authRepository.loginWithBiometric(activity)

            if (user != null) {
                _currentUser.value = user
                _isAuthenticated.value = true
                _error.value = null

                println("✅ Biometric JWT login successful for user: ${user.username}")
            } else {
                throw Exception("Biometric authentication failed")
            }

        } catch (error: Exception) {
            _error.value = when {
                error.message?.contains("biometric") == true -> 
                    "Biometric authentication failed or was cancelled"
                
                error.message?.contains("expired") == true -> {
                    authRepository.clearAuthTokens()
                    "Your stored login has expired. Please sign in again."
                }
                
                else -> "Biometric login failed: ${error.message}"
            }
            _isAuthenticated.value = false
        } finally {
            _isLoading.value = false
            isAuthenticating = false
        }
    }

    suspend fun register(username: String, password: String) {
        _isLoading.value = true

        try {
            val user = authRepository.register(username, password)
            _currentUser.value = user
            _isAuthenticated.value = true
            _error.value = null
        } catch (error: Exception) {
            println("🔍 Registration Error Details: $error")
            _error.value = when {
                error.message?.contains("connect") == true || 
                error.message?.contains("server") == true -> 
                    "Network issue. Try 'Continue as Guest' to play offline, or check your internet connection."
                
                error.message?.contains("exists") == true || 
                error.message?.contains("400") == true -> 
                    "Username already exists. Please choose a different username."
                
                else -> "Registration failed: ${error.message}"
            }
            _isAuthenticated.value = false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun logout() {
        _isLoading.value = true

        try {
            authRepository.logout()

            _currentUser.value = null
            _isAuthenticated.value = false
            _isGuestMode.value = false
            _biometricEnabled.value = false
            _error.value = null

            println("✅ JWT logout successful and all tokens cleared")

        } catch (error: Exception) {
            // Even if API logout fails, clear local tokens
            authRepository.clearAuthTokens()
            _currentUser.value = null
            _isAuthenticated.value = false
            _isGuestMode.value = false
            _biometricEnabled.value = false
            _error.value = "Logout completed with warning: ${error.message}"
        } finally {
            _isLoading.value = false
        }
    }

    fun continueAsGuest() {
        _currentUser.value = null
        _isAuthenticated.value = false
        _isGuestMode.value = true
        _error.value = null

        // Automatically enable offline mode when in guest mode
        // This could be handled by observing isGuestMode in the SudokuGameViewModel
    }

    suspend fun toggleBiometric(enabled: Boolean) {
        if (!authRepository.isBiometricAvailable()) {
            _error.value = "Biometric authentication is not available on this device"
            return
        }

        try {
            _biometricEnabled.value = enabled

            if (_isAuthenticated.value) {
                authRepository.updateTokensBiometricProtection(enabled)
            } else {
                authRepository.setBiometricEnabled(enabled)
            }

            println("✅ Biometric authentication ${if (enabled) "enabled" else "disabled"}")

        } catch (error: Exception) {
            _error.value = "Failed to update biometric setting: ${error.message}"
        }
    }

    fun clearError() {
        _error.value = null
    }

    suspend fun clearStoredCredentials() {
        try {
            authRepository.clearAuthTokens()
            _biometricEnabled.value = false
            _isAuthenticated.value = false
            _currentUser.value = null
            println("✅ Manually cleared all stored credentials and JWT tokens")
        } catch (error: Exception) {
            println("⚠️ Error clearing credentials: ${error.message}")
        }
    }

    suspend fun debugStoredCredentials() {
        try {
            val hasTokens = authRepository.hasStoredTokens()
            val biometricEnabled = authRepository.isBiometricEnabled()

            println("🔍 Debug Stored Authentication:")
            println("   - Has JWT Tokens: $hasTokens")
            println("   - Biometric Enabled: $biometricEnabled")
            println("   - Biometric Available: ${authRepository.isBiometricAvailable()}")

        } catch (error: Exception) {
            println("🔍 Debug Error: ${error.message}")
        }
    }
}