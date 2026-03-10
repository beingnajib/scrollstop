package com.scrollstop.app.security

import com.scrollstop.app.util.Constants

object PinState {
    @Volatile var isAuthenticated = false
        private set
    @Volatile var lastAuthTimestamp = 0L
        private set

    fun checkAuthValid(): Boolean {
        if (!isAuthenticated) return false
        if (System.currentTimeMillis() - lastAuthTimestamp > Constants.PIN_AUTH_TIMEOUT_MS) {
            isAuthenticated = false
            return false
        }
        return true
    }

    fun authenticate() {
        isAuthenticated = true
        lastAuthTimestamp = System.currentTimeMillis()
    }

    fun lock() {
        isAuthenticated = false
        lastAuthTimestamp = 0L
    }
}
