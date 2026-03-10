package com.scrollstop.app.detection

import android.view.accessibility.AccessibilityNodeInfo
import com.scrollstop.app.detection.platform.FacebookDetector
import com.scrollstop.app.detection.platform.InstagramDetector
import com.scrollstop.app.detection.platform.LinkedInDetector
import com.scrollstop.app.detection.platform.PlatformDetector
import com.scrollstop.app.detection.platform.RedditDetector
import com.scrollstop.app.detection.platform.SnapchatDetector
import com.scrollstop.app.detection.platform.TikTokDetector
import com.scrollstop.app.detection.platform.TwitterDetector
import com.scrollstop.app.detection.platform.YouTubeDetector
import com.scrollstop.app.util.Constants

class DetectionEngine {

    private val detectors: Map<String, PlatformDetector>

    init {
        val instagramDetector = InstagramDetector()
        val youtubeDetector = YouTubeDetector()
        val tiktokDetector = TikTokDetector()
        val facebookDetector = FacebookDetector()
        val snapchatDetector = SnapchatDetector()
        val redditDetector = RedditDetector()
        val twitterDetector = TwitterDetector()
        val linkedInDetector = LinkedInDetector()

        detectors = mapOf(
            Constants.PACKAGE_INSTAGRAM to instagramDetector,
            Constants.PACKAGE_YOUTUBE to youtubeDetector,
            Constants.PACKAGE_TIKTOK to tiktokDetector,
            Constants.PACKAGE_TIKTOK_ALT to tiktokDetector,
            Constants.PACKAGE_FACEBOOK to facebookDetector,
            Constants.PACKAGE_FACEBOOK_LITE to facebookDetector,
            Constants.PACKAGE_SNAPCHAT to snapchatDetector,
            Constants.PACKAGE_REDDIT to redditDetector,
            Constants.PACKAGE_TWITTER to twitterDetector,
            Constants.PACKAGE_LINKEDIN to linkedInDetector
        )
    }

    fun scan(packageName: String, rootNode: AccessibilityNodeInfo): DetectionResult {
        val detector = detectors[packageName] ?: return DetectionResult.NOT_DETECTED
        return detector.detect(rootNode)
    }
}
