package com.scrollstop.app.blocking

class PauseManager {

    @Volatile private var pauseEndTime: Long = 0L

    fun pause(durationMinutes: Int) {
        pauseEndTime = System.currentTimeMillis() + durationMinutes * 60 * 1000L
    }

    fun isPaused(): Boolean {
        if (pauseEndTime == 0L) return false
        if (System.currentTimeMillis() >= pauseEndTime) {
            pauseEndTime = 0L
            return false
        }
        return true
    }

    fun getRemainingPauseMs(): Long {
        if (!isPaused()) return 0L
        return (pauseEndTime - System.currentTimeMillis()).coerceAtLeast(0)
    }

    fun cancelPause() {
        pauseEndTime = 0L
    }
}
