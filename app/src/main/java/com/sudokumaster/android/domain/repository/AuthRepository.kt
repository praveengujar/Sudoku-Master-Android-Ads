package com.sudokumaster.android.domain.repository

import androidx.fragment.app.FragmentActivity
import com.sudokumaster.android.domain.model.User

interface AuthRepository {
    suspend fun login(username: String, password: String, enableBiometric: Boolean = false): User
    suspend fun register(username: String, password: String): User
    suspend fun logout()
    suspend fun getCurrentUser(): User?
    suspend fun loginWithBiometric(activity: FragmentActivity): User?
    suspend fun refreshAccessToken(): String?
    suspend fun getValidAccessToken(): String?
    fun hasStoredTokens(): Boolean
    suspend fun clearAuthTokens()
    suspend fun setBiometricEnabled(enabled: Boolean)
    suspend fun isBiometricEnabled(): Boolean
    fun isBiometricAvailable(): Boolean
    suspend fun updateTokensBiometricProtection(enabled: Boolean)
    suspend fun deleteUser(username: String): Boolean
}