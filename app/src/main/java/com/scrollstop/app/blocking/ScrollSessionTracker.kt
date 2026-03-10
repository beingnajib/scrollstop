package com.scrollstop.app.blocking

import com.scrollstop.app.data.model.AppTarget
import com.scrollstop.app.util.Constants

enum class BlockingMode { BLOCK_ALL, CURIOUS }
enum class SessionPhase { INACTIVE, SESSION_ACTIVE, COOLDOWN }

class ScrollSessionTracker(
    private var mode: BlockingMode = BlockingMode.BLOCK_ALL,
    private var sessionDurationMs: Long = Constants.DEFAULT_SESSION_DURATION_MS,
    private var cooldownDurationMs: Long = Constants.DEFAULT_COOLDOWN_DURATION_MS,
    private var allowOneInCooldown: Boolean = false
) {
    private data class SessionState(
        var phase: SessionPhase = SessionPhase.INACTIVE,
        var sessionStartTime: Long = 0L,
        var cooldownStartTime: Long = 0L,
        var oneVideoUsed: Boolean = false
    )

    private val sessions = mutableMapOf<AppTarget, SessionState>()
    private val pauseManager = PauseManager()

    fun setMode(newMode: BlockingMode) { mode = newMode }
    fun setSessionDuration(ms: Long) { sessionDurationMs = ms }
    fun setCooldownDuration(ms: Long) { cooldownDurationMs = ms }
    fun setAllowOneInCooldown(allow: Boolean) { allowOneInCooldown = allow }

    fun getPauseManager(): PauseManager = pauseManager

    fun onContentDetected(appTarget: AppTarget): SessionVerdict {
        // Pause overrides everything
        if (pauseManager.isPaused()) return SessionVerdict.ALLOW

        // Block All mode — always block immediately
        if (mode == BlockingMode.BLOCK_ALL) return SessionVerdict.BLOCK

        // Curious mode — time-based sessions
        val now = System.currentTimeMillis()
        val session = sessions.getOrPut(appTarget) { SessionState() }

        return when (session.phase) {
            SessionPhase.INACTIVE -> {
                // Start a new session
                session.phase = SessionPhase.SESSION_ACTIVE
                session.sessionStartTime = now
                session.oneVideoUsed = false
                SessionVerdict.ALLOW
            }

            SessionPhase.SESSION_ACTIVE -> {
                val elapsed = now - session.sessionStartTime
                if (elapsed >= sessionDurationMs) {
                    // Session expired — transition to cooldown
                    session.phase = SessionPhase.COOLDOWN
                    session.cooldownStartTime = now
                    session.oneVideoUsed = false
                    SessionVerdict.BLOCK
                } else {
                    SessionVerdict.ALLOW
                }
            }

            SessionPhase.COOLDOWN -> {
                val cooldownElapsed = now - session.cooldownStartTime
                if (cooldownElapsed >= cooldownDurationMs) {
                    // Cooldown expired — start fresh session
                    session.phase = SessionPhase.SESSION_ACTIVE
                    session.sessionStartTime = now
                    session.oneVideoUsed = false
                    SessionVerdict.ALLOW
                } else if (allowOneInCooldown && !session.oneVideoUsed) {
                    // Allow exactly one video during cooldown
                    session.oneVideoUsed = true
                    SessionVerdict.ALLOW
                } else {
                    SessionVerdict.BLOCK
                }
            }
        }
    }

    fun getPhase(appTarget: AppTarget): SessionPhase {
        return sessions[appTarget]?.phase ?: SessionPhase.INACTIVE
    }

    fun getSessionRemainingMs(appTarget: AppTarget): Long {
        val session = sessions[appTarget] ?: return 0
        if (session.phase != SessionPhase.SESSION_ACTIVE) return 0
        val elapsed = System.currentTimeMillis() - session.sessionStartTime
        return (sessionDurationMs - elapsed).coerceAtLeast(0)
    }

    fun getCooldownRemainingMs(appTarget: AppTarget): Long {
        val session = sessions[appTarget] ?: return 0
        if (session.phase != SessionPhase.COOLDOWN) return 0
        val elapsed = System.currentTimeMillis() - session.cooldownStartTime
        return (cooldownDurationMs - elapsed).coerceAtLeast(0)
    }

    fun reset(appTarget: AppTarget) { sessions.remove(appTarget) }
    fun resetAll() { sessions.clear() }
}

enum class SessionVerdict {
    ALLOW,
    BLOCK,
    ALREADY_BLOCKED
}
