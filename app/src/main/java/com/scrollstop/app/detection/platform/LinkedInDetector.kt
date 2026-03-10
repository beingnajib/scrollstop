package com.scrollstop.app.detection.platform

import android.view.accessibility.AccessibilityNodeInfo
import com.scrollstop.app.data.model.AppTarget
import com.scrollstop.app.detection.DetectionResult
import com.scrollstop.app.detection.DetectionRule
import com.scrollstop.app.detection.MatchType
import com.scrollstop.app.detection.NodeScanner
import com.scrollstop.app.util.Constants

class LinkedInDetector : PlatformDetector {

    override val targetPackage = Constants.PACKAGE_LINKEDIN

    companion object {
        private val VIDEO_PLAYER_VIEW_IDS = listOf(
            "com.linkedin.android:id/video_player_surface",
            "com.linkedin.android:id/video_player_view",
            "com.linkedin.android:id/feed_video_player",
            "com.linkedin.android:id/video_container"
        )

        private val VIDEO_CONTENT_PATTERN = Regex("(?i)(video|playing)")
    }

    override fun detect(rootNode: AccessibilityNodeInfo): DetectionResult {
        // Strategy 1: Check for known video player view IDs (most reliable)
        for (id in VIDEO_PLAYER_VIEW_IDS) {
            if (NodeScanner.findByViewId(rootNode, id)) {
                return DetectionResult(
                    detected = true,
                    appTarget = AppTarget.LINKEDIN,
                    matchedRule = DetectionRule(
                        MatchType.VIEW_ID, id,
                        appTarget = AppTarget.LINKEDIN,
                        description = "LinkedIn video player view ID: $id"
                    )
                )
            }
        }

        // Strategy 2: Vertical video pager + video surface
        if (NodeScanner.hasVerticalPager(rootNode) && NodeScanner.hasVideoSurface(rootNode)) {
            return DetectionResult(
                detected = true,
                appTarget = AppTarget.LINKEDIN,
                matchedRule = DetectionRule.HEURISTIC_VERTICAL_VIDEO,
                confidence = 0.8f
            )
        }

        // Strategy 3: Content description matching + video surface
        if (NodeScanner.findByContentDescription(rootNode, VIDEO_CONTENT_PATTERN)) {
            if (NodeScanner.hasVideoSurface(rootNode)) {
                return DetectionResult(
                    detected = true,
                    appTarget = AppTarget.LINKEDIN,
                    matchedRule = DetectionRule.HEURISTIC_CONTENT_DESC,
                    confidence = 0.7f
                )
            }
        }

        return DetectionResult.NOT_DETECTED
    }
}
