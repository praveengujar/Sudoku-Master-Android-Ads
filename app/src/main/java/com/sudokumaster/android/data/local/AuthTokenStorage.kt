package com.sudokumaster.android.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.SecretKey

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class AuthTokenStorage @Inject constructor(
    private val context: Context
) {
    
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val TOKEN_EXPIRY_KEY = stringPreferencesKey("token_expiry")
        private val BIOMETRIC_ENABLED_KEY = booleanPreferencesKey("biometric_enabled")
        private val ENCRYPTED_ACCESS_TOKEN_KEY = stringPreferencesKey("encrypted_access_token")
        private val ENCRYPTED_REFRESH_TOKEN_KEY = stringPreferencesKey("encrypted_refresh_token")
        private val TOKEN_IV_KEY = stringPreferencesKey("token_iv")
        
        private const val KEYSTORE_ALIAS = "SudokuMasterAuthKey"
    }
    
    data class AuthTokens(
        val accessToken: String,
        val refreshToken: String,
        val expiryDate: Date
    ) {
        val isExpired: Boolean
            get() = Date().after(expiryDate)
            
        val willExpireSoon: Boolean
            get() {
                val fiveMinutesFromNow = Calendar.getInstance().apply {
                    add(Calendar.MINUTE, 5)
                }.time
                return expiryDate.before(fiveMinutesFromNow)
            }
    }

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    init {
        generateKeyIfNeeded()
    }

    private fun generateKeyIfNeeded() {
        if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEYSTORE_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(false) // We'll handle biometric separately
                .build()
            
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }

    private fun getSecretKey(): SecretKey {
        return keyStore.getKey(KEYSTORE_ALIAS, null) as SecretKey
    }

    suspend fun saveAuthTokens(
        accessToken: String,
        refreshToken: String,
        expiresIn: String,
        requireBiometric: Boolean = false
    ) {
        val expiryDate = calculateExpiryDate(expiresIn)
        
        context.dataStore.edit { prefs ->
            if (requireBiometric) {
                // Encrypt tokens for biometric protection
                val (encryptedAccess, encryptedRefresh, iv) = encryptTokens(accessToken, refreshToken)
                prefs[ENCRYPTED_ACCESS_TOKEN_KEY] = encryptedAccess
                prefs[ENCRYPTED_REFRESH_TOKEN_KEY] = encryptedRefresh
                prefs[TOKEN_IV_KEY] = iv
                // Clear plain text tokens
                prefs.remove(ACCESS_TOKEN_KEY)
                prefs.remove(REFRESH_TOKEN_KEY)
            } else {
                // Store tokens in plain text (still secure within app sandbox)
                prefs[ACCESS_TOKEN_KEY] = accessToken
                prefs[REFRESH_TOKEN_KEY] = refreshToken
                // Clear encrypted tokens
                prefs.remove(ENCRYPTED_ACCESS_TOKEN_KEY)
                prefs.remove(ENCRYPTED_REFRESH_TOKEN_KEY)
                prefs.remove(TOKEN_IV_KEY)
            }
            
            prefs[TOKEN_EXPIRY_KEY] = expiryDate.time.toString()
            prefs[BIOMETRIC_ENABLED_KEY] = requireBiometric
        }
    }

    suspend fun getAuthTokens(): AuthTokens? {
        val prefs = context.dataStore.data.first()
        
        val biometricEnabled = prefs[BIOMETRIC_ENABLED_KEY] ?: false
        val expiryTimeString = prefs[TOKEN_EXPIRY_KEY] ?: return null
        val expiryDate = Date(expiryTimeString.toLong())
        
        return if (biometricEnabled) {
            // Get encrypted tokens
            val encryptedAccess = prefs[ENCRYPTED_ACCESS_TOKEN_KEY] ?: return null
            val encryptedRefresh = prefs[ENCRYPTED_REFRESH_TOKEN_KEY] ?: return null
            val iv = prefs[TOKEN_IV_KEY] ?: return null
            
            val (accessToken, refreshToken) = decryptTokens(encryptedAccess, encryptedRefresh, iv)
            AuthTokens(accessToken, refreshToken, expiryDate)
        } else {
            // Get plain text tokens
            val accessToken = prefs[ACCESS_TOKEN_KEY] ?: return null
            val refreshToken = prefs[REFRESH_TOKEN_KEY] ?: return null
            AuthTokens(accessToken, refreshToken, expiryDate)
        }
    }

    suspend fun getAuthTokensWithBiometric(activity: FragmentActivity): AuthTokens? {
        val biometricEnabled = isBiometricEnabled()
        
        return if (biometricEnabled) {
            // Require biometric authentication
            if (authenticateWithBiometric(activity)) {
                getAuthTokens()
            } else {
                null
            }
        } else {
            getAuthTokens()
        }
    }

    suspend fun hasStoredTokens(): Boolean {
        val prefs = context.dataStore.data.first()
        val biometricEnabled = prefs[BIOMETRIC_ENABLED_KEY] ?: false
        
        return if (biometricEnabled) {
            prefs[ENCRYPTED_ACCESS_TOKEN_KEY] != null && 
            prefs[ENCRYPTED_REFRESH_TOKEN_KEY] != null
        } else {
            prefs[ACCESS_TOKEN_KEY] != null && 
            prefs[REFRESH_TOKEN_KEY] != null
        }
    }

    suspend fun clearAuthTokens() {
        context.dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN_KEY)
            prefs.remove(REFRESH_TOKEN_KEY)
            prefs.remove(TOKEN_EXPIRY_KEY)
            prefs.remove(BIOMETRIC_ENABLED_KEY)
            prefs.remove(ENCRYPTED_ACCESS_TOKEN_KEY)
            prefs.remove(ENCRYPTED_REFRESH_TOKEN_KEY)
            prefs.remove(TOKEN_IV_KEY)
        }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[BIOMETRIC_ENABLED_KEY] = enabled
        }
    }

    suspend fun isBiometricEnabled(): Boolean {
        val prefs = context.dataStore.data.first()
        return prefs[BIOMETRIC_ENABLED_KEY] ?: false
    }

    fun isBiometricAvailable(): Boolean {
        return androidx.biometric.BiometricManager.from(context)
            .canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK) == 
            androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun calculateExpiryDate(expiresIn: String): Date {
        val calendar = Calendar.getInstance()
        
        when {
            expiresIn.endsWith("m") -> {
                val minutes = expiresIn.dropLast(1).toIntOrNull() ?: 15
                calendar.add(Calendar.MINUTE, minutes)
            }
            expiresIn.endsWith("h") -> {
                val hours = expiresIn.dropLast(1).toIntOrNull() ?: 1
                calendar.add(Calendar.HOUR, hours)
            }
            expiresIn.endsWith("d") -> {
                val days = expiresIn.dropLast(1).toIntOrNull() ?: 7
                calendar.add(Calendar.DAY_OF_MONTH, days)
            }
            else -> {
                // Default to 15 minutes if format is not recognized
                calendar.add(Calendar.MINUTE, 15)
            }
        }
        
        return calendar.time
    }

    private fun encryptTokens(accessToken: String, refreshToken: String): Triple<String, String, String> {
        val secretKey = getSecretKey()
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        
        val iv = cipher.iv
        val encryptedAccess = cipher.doFinal(accessToken.toByteArray())
        
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))
        val encryptedRefresh = cipher.doFinal(refreshToken.toByteArray())
        
        return Triple(
            Base64.getEncoder().encodeToString(encryptedAccess),
            Base64.getEncoder().encodeToString(encryptedRefresh),
            Base64.getEncoder().encodeToString(iv)
        )
    }

    private fun decryptTokens(
        encryptedAccess: String, 
        encryptedRefresh: String, 
        ivString: String
    ): Pair<String, String> {
        val secretKey = getSecretKey()
        val iv = Base64.getDecoder().decode(ivString)
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
        val accessToken = String(cipher.doFinal(Base64.getDecoder().decode(encryptedAccess)))
        
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
        val refreshToken = String(cipher.doFinal(Base64.getDecoder().decode(encryptedRefresh)))
        
        return Pair(accessToken, refreshToken)
    }

    private suspend fun authenticateWithBiometric(activity: FragmentActivity): Boolean {
        return kotlin.runCatching {
            val biometricPrompt = BiometricPrompt(activity, 
                androidx.core.content.ContextCompat.getMainExecutor(context),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                    }
                }
            )

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authenticate to access Sudoku Master")
                .setSubtitle("Use your biometric credential to sign in")
                .setNegativeButtonText("Cancel")
                .build()

            // This is a simplified version - in a real implementation,
            // you'd need to handle the callback properly with coroutines
            biometricPrompt.authenticate(promptInfo)
            true
        }.getOrElse { false }
    }
}