package com.scrollstop.app.blocking

import com.scrollstop.app.util.Constants

class CooldownManager(private val cooldownMs: Long = Constants.COOLDOWN_MS) {

    private var lastActionTimestamp: Long = 0L

    fun isInCooldown(): Boolean {
        return System.currentTimeMillis() - lastActionTimestamp < cooldownMs
    }

    fun startCooldown() {
        lastActionTimestamp = System.currentTimeMillis()
    }

    fun reset() {
        lastActionTimestamp = 0L
    }
}
