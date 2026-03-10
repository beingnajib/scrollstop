package com.scrollstop.app.data.model

import com.scrollstop.app.util.Constants

enum class AppTarget(val displayName: String, val packageNames: Set<String>) {
    INSTAGRAM_REELS("Instagram Reels", setOf(Constants.PACKAGE_INSTAGRAM)),
    YOUTUBE_SHORTS("YouTube Shorts", setOf(Constants.PACKAGE_YOUTUBE)),
    TIKTOK("TikTok", setOf(Constants.PACKAGE_TIKTOK, Constants.PACKAGE_TIKTOK_ALT)),
    FACEBOOK_REELS("Facebook Reels", setOf(Constants.PACKAGE_FACEBOOK, Constants.PACKAGE_FACEBOOK_LITE)),
    SNAPCHAT("Snapchat", setOf(Constants.PACKAGE_SNAPCHAT)),
    REDDIT("Reddit", setOf(Constants.PACKAGE_REDDIT)),
    TWITTER("X (Twitter)", setOf(Constants.PACKAGE_TWITTER)),
    LINKEDIN("LinkedIn", setOf(Constants.PACKAGE_LINKEDIN));

    companion object {
        fun fromPackageName(packageName: String): AppTarget? {
            return entries.find { packageName in it.packageNames }
        }
    }
}
