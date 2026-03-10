package com.scrollstop.app.detection.platform

import android.view.accessibility.AccessibilityNodeInfo
import com.scrollstop.app.data.model.AppTarget
import com.scrollstop.app.detection.DetectionResult
import com.scrollstop.app.detection.DetectionRule
import com.scrollstop.app.detection.MatchType
import com.scrollstop.app.detection.NodeScanner
import com.scrollstop.app.util.Constants

class TwitterDetector : PlatformDetector {

    override val targetPackage = Constants.PACKAGE_TWITTER

    companion object {
        private val VIDEO_PLAYER_VIEW_IDS = listOf(
            "com.twitter.android:id/video_player_view",
            "com.twitter.android:id/inline_video_player",
            "com.twitter.android:id/video_surface_view",
            "com.twitter.android:id/immersive_player_container",
            "com.twitter.android:id/full_screen_video_container"
        )

        private val VIDEO_CONTENT_PATTERN = Regex("(?i)(video|playing|immersive)")
    }

    override fun detect(rootNode: AccessibilityNodeInfo): DetectionResult {
        // Strategy 1: Check for known video player view IDs (most reliable)
        for (id in VIDEO_PLAYER_VIEW_IDS) {
            if (NodeScanner.findByViewId(rootNode, id)) {
                return DetectionResult(
                    detected = true,
                    appTarget = AppTarget.TWITTER,
                    matchedRule = DetectionRule(
                        MatchType.VIEW_ID, id,
                        appTarget = AppTarget.TWITTER,
                        description = "Twitter video player view ID: $id"
                    )
                )
            }
        }

        // Strategy 2: Full-screen vertical video feed
        if (NodeScanner.hasVerticalPager(rootNode) && NodeScanner.hasVideoSurface(rootNode)) {
            return DetectionResult(
                detected = true,
                appTarget = AppTarget.TWITTER,
                matchedRule = DetectionRule.HEURISTIC_VERTICAL_VIDEO,
                confidence = 0.8f
            )
        }

        // Strategy 3: Content description matching + video surface
        if (NodeScanner.findByContentDescription(rootNode, VIDEO_CONTENT_PATTERN)) {
            if (NodeScanner.hasVideoSurface(rootNode)) {
                return DetectionResult(
                    detected = true,
                    appTarget = AppTarget.TWITTER,
                    matchedRule = DetectionRule.HEURISTIC_CONTENT_DESC,
                    confidence = 0.7f
                )
            }
        }

        return DetectionResult.NOT_DETECTED
    }
}
