package com.scrollstop.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "scrollstop_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        val MASTER_ENABLED = booleanPreferencesKey("master_enabled")
        val BLOCK_INSTAGRAM_REELS = booleanPreferencesKey("block_instagram_reels")
        val BLOCK_YOUTUBE_SHORTS = booleanPreferencesKey("block_youtube_shorts")
        val BLOCK_TIKTOK = booleanPreferencesKey("block_tiktok")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val BLOCKING_MODE = stringPreferencesKey("blocking_mode")
        val OVERLAY_DURATION_MS = longPreferencesKey("overlay_duration_ms")
        val COOLDOWN_MS = longPreferencesKey("cooldown_ms")
        val INSTAGRAM_DAILY_ALLOWANCE_MINUTES = intPreferencesKey("ig_daily_allowance")
        val YOUTUBE_DAILY_ALLOWANCE_MINUTES = intPreferencesKey("yt_daily_allowance")
        val TIKTOK_DAILY_ALLOWANCE_MINUTES = intPreferencesKey("tt_daily_allowance")

        // Per-platform blocking
        val BLOCK_FACEBOOK = booleanPreferencesKey("block_facebook")
        val BLOCK_SNAPCHAT = booleanPreferencesKey("block_snapchat")
        val BLOCK_REDDIT = booleanPreferencesKey("block_reddit")
        val BLOCK_TWITTER = booleanPreferencesKey("block_twitter")
        val BLOCK_LINKEDIN = booleanPreferencesKey("block_linkedin")

        // Scroll block mode: "block_all" or "curious"
        val SCROLL_BLOCK_MODE = stringPreferencesKey("scroll_block_mode")

        // Session / cooldown
        val SESSION_DURATION_MINUTES = intPreferencesKey("session_duration_minutes")
        val COOLDOWN_DURATION_MINUTES = intPreferencesKey("cooldown_duration_minutes")
        val ALLOW_ONE_IN_COOLDOWN = booleanPreferencesKey("allow_one_in_cooldown")

        // Blocking action (replaces old BLOCKING_MODE)
        val BLOCKING_ACTION = stringPreferencesKey("blocking_action")

        // Pause
        val PAUSE_DURATION_MINUTES = intPreferencesKey("pause_duration_minutes")
        val MAX_PAUSE_MINUTES = intPreferencesKey("max_pause_minutes")

        // Smart session tracking
        val ALLOWED_VIDEOS_PER_SESSION = intPreferencesKey("allowed_videos_per_session")
        val SESSION_RESET_TIMEOUT_MINUTES = intPreferencesKey("session_reset_timeout_minutes")

        // Parental lock
        val PIN_HASH = stringPreferencesKey("pin_hash")
        val PIN_SALT = stringPreferencesKey("pin_salt")
        val PARENTAL_LOCK_ENABLED = booleanPreferencesKey("parental_lock_enabled")
        val PIN_FAILED_ATTEMPTS = intPreferencesKey("pin_failed_attempts")
        val PIN_LOCKOUT_UNTIL = longPreferencesKey("pin_lockout_until")
        val PIN_RESET_CODE_HASH = stringPreferencesKey("pin_reset_code_hash")

        // Streak tracking
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        val BEST_STREAK = intPreferencesKey("best_streak")
        val LAST_ACTIVE_DATE = stringPreferencesKey("last_active_date")
    }

    // Flow getters
    val masterEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[MASTER_ENABLED] ?: true }

    val blockInstagramReels: Flow<Boolean> = context.dataStore.data
        .map { it[BLOCK_INSTAGRAM_REELS] ?: true }

    val blockYoutubeShorts: Flow<Boolean> = context.dataStore.data
        .map { it[BLOCK_YOUTUBE_SHORTS] ?: true }

    val blockTiktok: Flow<Boolean> = context.dataStore.data
        .map { it[BLOCK_TIKTOK] ?: true }

    val blockFacebook: Flow<Boolean> = context.dataStore.data
        .map { it[BLOCK_FACEBOOK] ?: true }

    val blockSnapchat: Flow<Boolean> = context.dataStore.data
        .map { it[BLOCK_SNAPCHAT] ?: true }

    val blockReddit: Flow<Boolean> = context.dataStore.data
        .map { it[BLOCK_REDDIT] ?: true }

    val blockTwitter: Flow<Boolean> = context.dataStore.data
        .map { it[BLOCK_TWITTER] ?: true }

    val blockLinkedIn: Flow<Boolean> = context.dataStore.data
        .map { it[BLOCK_LINKEDIN] ?: true }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { it[ONBOARDING_COMPLETED] ?: false }

    val scrollBlockMode: Flow<String> = context.dataStore.data
        .map { it[SCROLL_BLOCK_MODE] ?: "block_all" }

    val blockingMode: Flow<String> = context.dataStore.data
        .map { it[BLOCKING_MODE] ?: "overlay_and_back" }

    val blockingAction: Flow<String> = context.dataStore.data
        .map { it[BLOCKING_ACTION] ?: "close_reel" }

    val sessionDurationMinutes: Flow<Int> = context.dataStore.data
        .map { it[SESSION_DURATION_MINUTES] ?: 3 }

    val cooldownDurationMinutes: Flow<Int> = context.dataStore.data
        .map { it[COOLDOWN_DURATION_MINUTES] ?: 30 }

    val allowOneInCooldown: Flow<Boolean> = context.dataStore.data
        .map { it[ALLOW_ONE_IN_COOLDOWN] ?: false }

    val pauseDurationMinutes: Flow<Int> = context.dataStore.data
        .map { it[PAUSE_DURATION_MINUTES] ?: 5 }

    val maxPauseMinutes: Flow<Int> = context.dataStore.data
        .map { it[MAX_PAUSE_MINUTES] ?: 15 }

    val instagramDailyAllowance: Flow<Int> = context.dataStore.data
        .map { it[INSTAGRAM_DAILY_ALLOWANCE_MINUTES] ?: 0 }

    val youtubeDailyAllowance: Flow<Int> = context.dataStore.data
        .map { it[YOUTUBE_DAILY_ALLOWANCE_MINUTES] ?: 0 }

    val tiktokDailyAllowance: Flow<Int> = context.dataStore.data
        .map { it[TIKTOK_DAILY_ALLOWANCE_MINUTES] ?: 0 }

    // Smart session tracking
    val allowedVideosPerSession: Flow<Int> = context.dataStore.data
        .map { it[ALLOWED_VIDEOS_PER_SESSION] ?: 3 }

    val sessionResetTimeoutMinutes: Flow<Int> = context.dataStore.data
        .map { it[SESSION_RESET_TIMEOUT_MINUTES] ?: 5 }

    // Parental lock
    val parentalLockEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[PARENTAL_LOCK_ENABLED] ?: false }

    val pinHash: Flow<String?> = context.dataStore.data
        .map { it[PIN_HASH] }

    val pinSalt: Flow<String?> = context.dataStore.data
        .map { it[PIN_SALT] }

    val pinFailedAttempts: Flow<Int> = context.dataStore.data
        .map { it[PIN_FAILED_ATTEMPTS] ?: 0 }

    val pinLockoutUntil: Flow<Long> = context.dataStore.data
        .map { it[PIN_LOCKOUT_UNTIL] ?: 0L }

    val pinResetCodeHash: Flow<String?> = context.dataStore.data
        .map { it[PIN_RESET_CODE_HASH] }

    // Streak
    val currentStreak: Flow<Int> = context.dataStore.data
        .map { it[CURRENT_STREAK] ?: 0 }

    val bestStreak: Flow<Int> = context.dataStore.data
        .map { it[BEST_STREAK] ?: 0 }

    val lastActiveDate: Flow<String?> = context.dataStore.data
        .map { it[LAST_ACTIVE_DATE] }

    // Setters
    suspend fun setMasterEnabled(enabled: Boolean) {
        context.dataStore.edit { it[MASTER_ENABLED] = enabled }
    }

    suspend fun setBlockInstagramReels(enabled: Boolean) {
        context.dataStore.edit { it[BLOCK_INSTAGRAM_REELS] = enabled }
    }

    suspend fun setBlockYoutubeShorts(enabled: Boolean) {
        context.dataStore.edit { it[BLOCK_YOUTUBE_SHORTS] = enabled }
    }

    suspend fun setBlockTiktok(enabled: Boolean) {
        context.dataStore.edit { it[BLOCK_TIKTOK] = enabled }
    }

    suspend fun setBlockFacebook(enabled: Boolean) {
        context.dataStore.edit { it[BLOCK_FACEBOOK] = enabled }
    }

    suspend fun setBlockSnapchat(enabled: Boolean) {
        context.dataStore.edit { it[BLOCK_SNAPCHAT] = enabled }
    }

    suspend fun setBlockReddit(enabled: Boolean) {
        context.dataStore.edit { it[BLOCK_REDDIT] = enabled }
    }

    suspend fun setBlockTwitter(enabled: Boolean) {
        context.dataStore.edit { it[BLOCK_TWITTER] = enabled }
    }

    suspend fun setBlockLinkedIn(enabled: Boolean) {
        context.dataStore.edit { it[BLOCK_LINKEDIN] = enabled }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setScrollBlockMode(mode: String) {
        context.dataStore.edit { it[SCROLL_BLOCK_MODE] = mode }
    }

    suspend fun setBlockingMode(mode: String) {
        context.dataStore.edit { it[BLOCKING_MODE] = mode }
    }

    suspend fun setBlockingAction(action: String) {
        context.dataStore.edit { it[BLOCKING_ACTION] = action }
    }

    suspend fun setSessionDurationMinutes(minutes: Int) {
        context.dataStore.edit { it[SESSION_DURATION_MINUTES] = minutes }
    }

    suspend fun setCooldownDurationMinutes(minutes: Int) {
        context.dataStore.edit { it[COOLDOWN_DURATION_MINUTES] = minutes }
    }

    suspend fun setAllowOneInCooldown(allow: Boolean) {
        context.dataStore.edit { it[ALLOW_ONE_IN_COOLDOWN] = allow }
    }

    suspend fun setPauseDurationMinutes(minutes: Int) {
        context.dataStore.edit { it[PAUSE_DURATION_MINUTES] = minutes }
    }

    suspend fun setMaxPauseMinutes(minutes: Int) {
        context.dataStore.edit { it[MAX_PAUSE_MINUTES] = minutes }
    }

    suspend fun setInstagramDailyAllowance(minutes: Int) {
        context.dataStore.edit { it[INSTAGRAM_DAILY_ALLOWANCE_MINUTES] = minutes }
    }

    suspend fun setYoutubeDailyAllowance(minutes: Int) {
        context.dataStore.edit { it[YOUTUBE_DAILY_ALLOWANCE_MINUTES] = minutes }
    }

    suspend fun setTiktokDailyAllowance(minutes: Int) {
        context.dataStore.edit { it[TIKTOK_DAILY_ALLOWANCE_MINUTES] = minutes }
    }

    // Smart session tracking setters
    suspend fun setAllowedVideosPerSession(count: Int) {
        context.dataStore.edit { it[ALLOWED_VIDEOS_PER_SESSION] = count }
    }

    suspend fun setSessionResetTimeoutMinutes(minutes: Int) {
        context.dataStore.edit { it[SESSION_RESET_TIMEOUT_MINUTES] = minutes }
    }

    // Parental lock setters
    suspend fun setParentalLockEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PARENTAL_LOCK_ENABLED] = enabled }
    }

    suspend fun setPinHash(hash: String) {
        context.dataStore.edit { it[PIN_HASH] = hash }
    }

    suspend fun setPinSalt(salt: String) {
        context.dataStore.edit { it[PIN_SALT] = salt }
    }

    suspend fun setPinFailedAttempts(count: Int) {
        context.dataStore.edit { it[PIN_FAILED_ATTEMPTS] = count }
    }

    suspend fun setPinLockoutUntil(timestamp: Long) {
        context.dataStore.edit { it[PIN_LOCKOUT_UNTIL] = timestamp }
    }

    suspend fun setPinResetCodeHash(hash: String) {
        context.dataStore.edit { it[PIN_RESET_CODE_HASH] = hash }
    }

    suspend fun clearPin() {
        context.dataStore.edit {
            it.remove(PIN_HASH)
            it.remove(PIN_SALT)
            it.remove(PIN_RESET_CODE_HASH)
            it[PARENTAL_LOCK_ENABLED] = false
            it[PIN_FAILED_ATTEMPTS] = 0
            it[PIN_LOCKOUT_UNTIL] = 0L
        }
    }

    // Streak setters
    suspend fun setCurrentStreak(streak: Int) {
        context.dataStore.edit { it[CURRENT_STREAK] = streak }
    }

    suspend fun setBestStreak(streak: Int) {
        context.dataStore.edit { it[BEST_STREAK] = streak }
    }

    suspend fun setLastActiveDate(date: String) {
        context.dataStore.edit { it[LAST_ACTIVE_DATE] = date }
    }
}
