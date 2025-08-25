package com.sudokumaster.android.data.repository

import androidx.fragment.app.FragmentActivity
import com.sudokumaster.android.data.local.AuthTokenStorage
import com.sudokumaster.android.data.remote.ApiService
import com.sudokumaster.android.data.remote.LoginRequest
import com.sudokumaster.android.data.remote.RegisterRequest
import com.sudokumaster.android.domain.model.LogoutRequest
import com.sudokumaster.android.domain.model.RefreshTokenRequest
import com.sudokumaster.android.domain.model.User
import com.sudokumaster.android.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val authTokenStorage: AuthTokenStorage
) : AuthRepository {

    override suspend fun login(username: String, password: String, enableBiometric: Boolean): User {
        return withContext(Dispatchers.IO) {
            val request = LoginRequest(username, password)
            val response = apiService.login(request)
            
            if (response.isSuccessful) {
                val authResponse = response.body() 
                    ?: throw Exception("Empty response from server")
                
                // Save JWT tokens
                authTokenStorage.saveAuthTokens(
                    accessToken = authResponse.accessToken,
                    refreshToken = authResponse.refreshToken,
                    expiresIn = authResponse.expiresIn,
                    requireBiometric = enableBiometric
                )
                
                println("✅ JWT tokens saved successfully")
                authResponse.user
            } else {
                throw Exception("Login failed: ${response.code()} ${response.message()}")
            }
        }
    }

    override suspend fun register(username: String, password: String): User {
        return withContext(Dispatchers.IO) {
            val request = RegisterRequest(username, password)
            val response = apiService.register(request)
            
            if (response.isSuccessful) {
                val authResponse = response.body() 
                    ?: throw Exception("Empty response from server")
                
                // Save JWT tokens (registration also returns tokens)
                authTokenStorage.saveAuthTokens(
                    accessToken = authResponse.accessToken,
                    refreshToken = authResponse.refreshToken,
                    expiresIn = authResponse.expiresIn,
                    requireBiometric = false
                )
                
                authResponse.user
            } else {
                throw Exception("Registration failed: ${response.code()} ${response.message()}")
            }
        }
    }

    override suspend fun logout() {
        withContext(Dispatchers.IO) {
            try {
                val tokens = authTokenStorage.getAuthTokens()
                
                if (tokens != null) {
                    val request = LogoutRequest(tokens.refreshToken)
                    val response = apiService.logout(
                        bearerToken = "Bearer ${tokens.accessToken}",
                        request = request
                    )
                    
                    if (!response.isSuccessful) {
                        println("⚠️ Server logout failed: ${response.code()} ${response.message()}")
                    } else {
                        println("✅ Server logout successful")
                    }
                }
            } catch (error: Exception) {
                println("⚠️ Error during server logout: ${error.message}")
                // Continue with local cleanup even if server logout fails
            } finally {
                // Always clear local tokens
                authTokenStorage.clearAuthTokens()
                println("✅ Local JWT tokens cleared")
            }
        }
    }

    override suspend fun getCurrentUser(): User? {
        return withContext(Dispatchers.IO) {
            try {
                val accessToken = getValidAccessToken()
                    ?: throw Exception("No valid access token available")
                
                val response = apiService.getCurrentUser("Bearer $accessToken")
                
                if (response.isSuccessful) {
                    response.body()
                } else {
                    throw Exception("Failed to get current user: ${response.code()} ${response.message()}")
                }
            } catch (error: Exception) {
                println("⚠️ Error getting current user: ${error.message}")
                null
            }
        }
    }

    override suspend fun loginWithBiometric(activity: FragmentActivity): User? {
        return withContext(Dispatchers.IO) {
            val tokens = authTokenStorage.getAuthTokensWithBiometric(activity)
                ?: throw Exception("No stored authentication tokens found or biometric authentication failed")
            
            println("✅ Retrieved JWT tokens via biometric authentication")
            
            // Verify tokens are still valid by getting current user
            val response = apiService.getCurrentUser("Bearer ${tokens.accessToken}")
            
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty user response")
            } else {
                throw Exception("Stored tokens are invalid")
            }
        }
    }

    override suspend fun refreshAccessToken(): String? {
        return withContext(Dispatchers.IO) {
            try {
                val tokens = authTokenStorage.getAuthTokens()
                    ?: throw Exception("No stored tokens to refresh")
                
                val request = RefreshTokenRequest(tokens.refreshToken)
                val response = apiService.refreshToken(request)
                
                if (response.isSuccessful) {
                    val refreshResponse = response.body()
                        ?: throw Exception("Empty refresh response")
                    
                    // Update stored access token
                    authTokenStorage.saveAuthTokens(
                        accessToken = refreshResponse.accessToken,
                        refreshToken = tokens.refreshToken, // Keep existing refresh token
                        expiresIn = refreshResponse.expiresIn,
                        requireBiometric = authTokenStorage.isBiometricEnabled()
                    )
                    
                    println("✅ Access token refreshed successfully")
                    refreshResponse.accessToken
                } else {
                    throw Exception("Token refresh failed: ${response.code()} ${response.message()}")
                }
            } catch (error: Exception) {
                println("⚠️ Token refresh failed: ${error.message}")
                // Clear invalid tokens
                authTokenStorage.clearAuthTokens()
                null
            }
        }
    }

    override suspend fun getValidAccessToken(): String? {
        return withContext(Dispatchers.IO) {
            val tokens = authTokenStorage.getAuthTokens() ?: return@withContext null
            
            when {
                tokens.isExpired -> {
                    println("🔄 Access token expired, attempting refresh...")
                    refreshAccessToken()
                }
                tokens.willExpireSoon -> {
                    println("🔄 Access token will expire soon, refreshing preemptively...")
                    refreshAccessToken() ?: tokens.accessToken
                }
                else -> {
                    tokens.accessToken
                }
            }
        }
    }

    override fun hasStoredTokens(): Boolean {
        return runCatching {
            kotlinx.coroutines.runBlocking {
                authTokenStorage.hasStoredTokens()
            }
        }.getOrElse { false }
    }

    override suspend fun clearAuthTokens() {
        authTokenStorage.clearAuthTokens()
    }

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        authTokenStorage.setBiometricEnabled(enabled)
    }

    override suspend fun isBiometricEnabled(): Boolean {
        return authTokenStorage.isBiometricEnabled()
    }

    override fun isBiometricAvailable(): Boolean {
        return authTokenStorage.isBiometricAvailable()
    }

    override suspend fun updateTokensBiometricProtection(enabled: Boolean) {
        val tokens = authTokenStorage.getAuthTokens()
            ?: throw Exception("No tokens available to update")
        
        // Re-save tokens with new biometric setting
        authTokenStorage.saveAuthTokens(
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken,
            expiresIn = "15m", // Use default expiry since we can't know original value
            requireBiometric = enabled
        )
        
        println("✅ Updated JWT token biometric protection: $enabled")
    }
}