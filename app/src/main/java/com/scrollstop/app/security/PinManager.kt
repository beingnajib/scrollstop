package com.scrollstop.app.security

import com.scrollstop.app.data.PreferencesManager
import com.scrollstop.app.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.security.SecureRandom

class PinManager(private val preferencesManager: PreferencesManager) {

    val isPinConfigured: Flow<Boolean> = preferencesManager.pinHash.map { it != null }
    val isParentalLockEnabled: Flow<Boolean> = preferencesManager.parentalLockEnabled

    suspend fun setPin(pin: String): String {
        val salt = generateSalt()
        val hash = hashPin(pin, salt)
        val resetCode = generateResetCode()
        val resetCodeHash = hashPin(resetCode, salt)

        preferencesManager.setPinSalt(salt)
        preferencesManager.setPinHash(hash)
        preferencesManager.setPinResetCodeHash(resetCodeHash)
        preferencesManager.setParentalLockEnabled(true)
        preferencesManager.setPinFailedAttempts(0)
        preferencesManager.setPinLockoutUntil(0L)

        return resetCode
    }

    suspend fun verifyPin(pin: String): PinVerifyResult {
        val lockoutUntil = preferencesManager.pinLockoutUntil.first()
        if (System.currentTimeMillis() < lockoutUntil) {
            return PinVerifyResult.LOCKED_OUT
        }

        val storedHash = preferencesManager.pinHash.first() ?: return PinVerifyResult.INCORRECT
        val salt = preferencesManager.pinSalt.first() ?: return PinVerifyResult.INCORRECT
        val attemptHash = hashPin(pin, salt)

        if (attemptHash == storedHash) {
            preferencesManager.setPinFailedAttempts(0)
            preferencesManager.setPinLockoutUntil(0L)
            PinState.authenticate()
            return PinVerifyResult.CORRECT
        }

        val attempts = preferencesManager.pinFailedAttempts.first() + 1
        preferencesManager.setPinFailedAttempts(attempts)

        if (attempts >= Constants.PIN_MAX_ATTEMPTS) {
            preferencesManager.setPinLockoutUntil(
                System.currentTimeMillis() + Constants.PIN_LOCKOUT_DURATION_MS
            )
            preferencesManager.setPinFailedAttempts(0)
            return PinVerifyResult.LOCKED_OUT
        }

        return PinVerifyResult.INCORRECT
    }

    suspend fun verifyResetCode(code: String): Boolean {
        val salt = preferencesManager.pinSalt.first() ?: return false
        val storedHash = preferencesManager.pinResetCodeHash.first() ?: return false
        val codeHash = hashPin(code, salt)

        if (codeHash == storedHash) {
            preferencesManager.clearPin()
            PinState.lock()
            return true
        }
        return false
    }

    suspend fun clearPin() {
        preferencesManager.clearPin()
        PinState.lock()
    }

    suspend fun getRemainingAttempts(): Int {
        val failed = preferencesManager.pinFailedAttempts.first()
        return (Constants.PIN_MAX_ATTEMPTS - failed).coerceAtLeast(0)
    }

    suspend fun getLockoutRemainingMs(): Long {
        val lockoutUntil = preferencesManager.pinLockoutUntil.first()
        return (lockoutUntil - System.currentTimeMillis()).coerceAtLeast(0)
    }

    companion object {
        fun hashPin(pin: String, salt: String): String {
            val digest = MessageDigest.getInstance("SHA-256")
            val bytes = digest.digest("$salt:$pin".toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        private fun generateSalt(): String {
            val bytes = ByteArray(16)
            SecureRandom().nextBytes(bytes)
            return bytes.joinToString("") { "%02x".format(it) }
        }

        private fun generateResetCode(): String {
            val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
            val random = SecureRandom()
            return (1..8).map { chars[random.nextInt(chars.length)] }.joinToString("")
        }
    }
}

enum class PinVerifyResult {
    CORRECT,
    INCORRECT,
    LOCKED_OUT
}
