package com.scrollstop.app.util

object Constants {
    // Target app package names
    const val PACKAGE_INSTAGRAM = "com.instagram.android"
    const val PACKAGE_YOUTUBE = "com.google.android.youtube"
    const val PACKAGE_TIKTOK = "com.zhiliaoapp.musically"
    const val PACKAGE_TIKTOK_ALT = "com.ss.android.ugc.trill"
    const val PACKAGE_FACEBOOK = "com.facebook.katana"
    const val PACKAGE_FACEBOOK_LITE = "com.facebook.lite"
    const val PACKAGE_SNAPCHAT = "com.snapchat.android"
    const val PACKAGE_REDDIT = "com.reddit.frontpage"
    const val PACKAGE_TWITTER = "com.twitter.android"
    const val PACKAGE_LINKEDIN = "com.linkedin.android"

    val TARGET_PACKAGES = setOf(
        PACKAGE_INSTAGRAM,
        PACKAGE_YOUTUBE,
        PACKAGE_TIKTOK,
        PACKAGE_TIKTOK_ALT,
        PACKAGE_FACEBOOK,
        PACKAGE_FACEBOOK_LITE,
        PACKAGE_SNAPCHAT,
        PACKAGE_REDDIT,
        PACKAGE_TWITTER,
        PACKAGE_LINKEDIN
    )

    // Debounce and cooldown
    const val DEBOUNCE_MS = 300L
    const val COOLDOWN_MS = 2000L
    const val OVERLAY_AUTO_DISMISS_MS = 3000L
    const val OVERLAY_SHOW_DELAY_MS = 50L

    // Node scanning
    const val MAX_SCAN_DEPTH = 15
    const val FINGERPRINT_MAX_NODES = 5
    const val FINGERPRINT_MAX_DEPTH = 8

    // Session tracking (v3 — time-based)
    const val DEFAULT_SESSION_DURATION_MS = 3 * 60 * 1000L // 3 minutes
    const val DEFAULT_COOLDOWN_DURATION_MS = 30 * 60 * 1000L // 30 minutes
    const val DEFAULT_MAX_PAUSE_MINUTES = 15
    const val DEFAULT_PAUSE_DURATION_MINUTES = 5

    // Allowance
    const val DEFAULT_ALLOWANCE_MINUTES = 0
    const val ESTIMATED_SESSION_MINUTES = 15L

    // Parental lock
    const val PIN_MAX_ATTEMPTS = 5
    const val PIN_LOCKOUT_DURATION_MS = 30_000L
    const val PIN_AUTH_TIMEOUT_MS = 2 * 60 * 1000L

    // Notification IDs
    const val NOTIFICATION_ID_SERVICE = 1
    const val NOTIFICATION_ID_BLOCK = 2
    const val NOTIFICATION_ID_REPORT = 3

    // Notification channel IDs
    const val CHANNEL_SERVICE = "scrollstop_service"
    const val CHANNEL_BLOCKS = "scrollstop_blocks"
    const val CHANNEL_REPORTS = "scrollstop_reports"
}
