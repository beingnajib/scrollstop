package com.scrollstop.app.detection.platform

import android.view.accessibility.AccessibilityNodeInfo
import com.scrollstop.app.data.model.AppTarget
import com.scrollstop.app.detection.DetectionResult
import com.scrollstop.app.detection.DetectionRule
import com.scrollstop.app.detection.MatchType
import com.scrollstop.app.detection.NodeScanner
import com.scrollstop.app.util.Constants

class YouTubeDetector : PlatformDetector {

    override val targetPackage = Constants.PACKAGE_YOUTUBE

    companion object {
        private val SHORTS_PLAYER_IDS = listOf(
            "com.google.android.youtube:id/reel_player_page_container",
            "com.google.android.youtube:id/reel_recycler",
            "com.google.android.youtube:id/shorts_player_container",
            "com.google.android.youtube:id/reel_watch_player"
        )

        private val SHORTS_TAB_PATTERN = Regex("(?i)^Shorts$")
    }

    override fun detect(rootNode: AccessibilityNodeInfo): DetectionResult {
        // Strategy 1: Direct view ID check for Shorts player (most reliable)
        for (id in SHORTS_PLAYER_IDS) {
            if (NodeScanner.findByViewId(rootNode, id)) {
                return DetectionResult(
                    detected = true,
                    appTarget = AppTarget.YOUTUBE_SHORTS,
                    matchedRule = DetectionRule(
                        MatchType.VIEW_ID, id,
                        appTarget = AppTarget.YOUTUBE_SHORTS,
                        description = "YouTube Shorts view ID: $id"
                    )
                )
            }
        }

        // Strategy 2: Selected "Shorts" tab in bottom navigation
        if (NodeScanner.findSelectedNodeWithText(rootNode, SHORTS_TAB_PATTERN)) {
            return DetectionResult(
                detected = true,
                appTarget = AppTarget.YOUTUBE_SHORTS,
                matchedRule = DetectionRule.HEURISTIC_SHORTS_TAB,
                confidence = 0.9f
            )
        }

        return DetectionResult.NOT_DETECTED
    }
}
