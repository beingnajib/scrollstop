package com.scrollstop.app.detection.platform

import android.view.accessibility.AccessibilityNodeInfo
import com.scrollstop.app.data.model.AppTarget
import com.scrollstop.app.detection.DetectionResult
import com.scrollstop.app.detection.DetectionRule
import com.scrollstop.app.detection.MatchType
import com.scrollstop.app.detection.NodeScanner
import com.scrollstop.app.util.Constants

class RedditDetector : PlatformDetector {

    override val targetPackage = Constants.PACKAGE_REDDIT

    companion object {
        private val VIDEO_PLAYER_VIEW_IDS = listOf(
            "com.reddit.frontpage:id/video_player",
            "com.reddit.frontpage:id/video_player_container",
            "com.reddit.frontpage:id/video_surface_view",
            "com.reddit.frontpage:id/player_view",
            "com.reddit.frontpage:id/reel_viewer_pager"
        )

        private val VIDEO_CONTENT_PATTERN = Regex("(?i)(video.?player|watch|playing)")
    }

    override fun detect(rootNode: AccessibilityNodeInfo): DetectionResult {
        // Strategy 1: Check for known video player view IDs (most reliable)
        for (id in VIDEO_PLAYER_VIEW_IDS) {
            if (NodeScanner.findByViewId(rootNode, id)) {
                return DetectionResult(
                    detected = true,
                    appTarget = AppTarget.REDDIT,
                    matchedRule = DetectionRule(
                        MatchType.VIEW_ID, id,
                        appTarget = AppTarget.REDDIT,
                        description = "Reddit video player view ID: $id"
                    )
                )
            }
        }

        // Strategy 2: Full-screen video surface heuristic
        if (NodeScanner.hasVerticalPager(rootNode) && NodeScanner.hasVideoSurface(rootNode)) {
            return DetectionResult(
                detected = true,
                appTarget = AppTarget.REDDIT,
                matchedRule = DetectionRule.HEURISTIC_VERTICAL_VIDEO,
                confidence = 0.8f
            )
        }

        // Strategy 3: Content description matching + video surface
        if (NodeScanner.findByContentDescription(rootNode, VIDEO_CONTENT_PATTERN)) {
            if (NodeScanner.hasVideoSurface(rootNode)) {
                return DetectionResult(
                    detected = true,
                    appTarget = AppTarget.REDDIT,
                    matchedRule = DetectionRule.HEURISTIC_CONTENT_DESC,
                    confidence = 0.7f
                )
            }
        }

        return DetectionResult.NOT_DETECTED
    }
}
