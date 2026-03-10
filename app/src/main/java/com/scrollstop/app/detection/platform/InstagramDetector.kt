package com.scrollstop.app.detection.platform

import android.view.accessibility.AccessibilityNodeInfo
import com.scrollstop.app.data.model.AppTarget
import com.scrollstop.app.detection.DetectionResult
import com.scrollstop.app.detection.DetectionRule
import com.scrollstop.app.detection.MatchType
import com.scrollstop.app.detection.NodeScanner
import com.scrollstop.app.util.Constants

class InstagramDetector : PlatformDetector {

    override val targetPackage = Constants.PACKAGE_INSTAGRAM

    companion object {
        private val REELS_VIEW_IDS = listOf(
            "com.instagram.android:id/clips_tab",
            "com.instagram.android:id/tab_clips",
            "com.instagram.android:id/clips_viewer_view_pager",
            "com.instagram.android:id/clips_swipe_refresh_container"
        )

        private val REELS_CONTENT_PATTERN = Regex("(?i)(reel|reels|clips)")
    }

    override fun detect(rootNode: AccessibilityNodeInfo): DetectionResult {
        // Strategy 1: Check for known Reels view IDs (fastest, O(1))
        for (id in REELS_VIEW_IDS) {
            if (NodeScanner.findByViewId(rootNode, id)) {
                return DetectionResult(
                    detected = true,
                    appTarget = AppTarget.INSTAGRAM_REELS,
                    matchedRule = DetectionRule(
                        MatchType.VIEW_ID, id,
                        appTarget = AppTarget.INSTAGRAM_REELS,
                        description = "Instagram Reels view ID: $id"
                    )
                )
            }
        }

        // Strategy 2: Structural heuristic — vertical ViewPager + video surface
        if (NodeScanner.hasVerticalPager(rootNode) && NodeScanner.hasVideoSurface(rootNode)) {
            return DetectionResult(
                detected = true,
                appTarget = AppTarget.INSTAGRAM_REELS,
                matchedRule = DetectionRule.HEURISTIC_VERTICAL_VIDEO,
                confidence = 0.8f
            )
        }

        // Strategy 3: Content description matching for "Reel" keyword
        if (NodeScanner.findByContentDescription(rootNode, REELS_CONTENT_PATTERN)) {
            // Additional check: make sure there's a video surface too
            if (NodeScanner.hasVideoSurface(rootNode)) {
                return DetectionResult(
                    detected = true,
                    appTarget = AppTarget.INSTAGRAM_REELS,
                    matchedRule = DetectionRule.HEURISTIC_CONTENT_DESC,
                    confidence = 0.7f
                )
            }
        }

        return DetectionResult.NOT_DETECTED
    }
}
