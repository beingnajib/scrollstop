package com.scrollstop.app.detection.platform

import android.view.accessibility.AccessibilityNodeInfo
import com.scrollstop.app.data.model.AppTarget
import com.scrollstop.app.detection.DetectionResult
import com.scrollstop.app.detection.DetectionRule
import com.scrollstop.app.detection.MatchType
import com.scrollstop.app.detection.NodeScanner
import com.scrollstop.app.util.Constants

class SnapchatDetector : PlatformDetector {

    override val targetPackage = Constants.PACKAGE_SNAPCHAT

    companion object {
        private val SPOTLIGHT_VIEW_IDS = listOf(
            "com.snapchat.android:id/spotlight_feed_container",
            "com.snapchat.android:id/spotlight_player_view",
            "com.snapchat.android:id/spotlight_video_surface",
            "com.snapchat.android:id/spotlight_pager"
        )

        private val SPOTLIGHT_CONTENT_PATTERN = Regex("(?i)(spotlight)")

        private val SPOTLIGHT_TAB_PATTERN = Regex("(?i)^Spotlight$")
    }

    override fun detect(rootNode: AccessibilityNodeInfo): DetectionResult {
        // Strategy 1: Check for known Spotlight view IDs (most reliable)
        for (id in SPOTLIGHT_VIEW_IDS) {
            if (NodeScanner.findByViewId(rootNode, id)) {
                return DetectionResult(
                    detected = true,
                    appTarget = AppTarget.SNAPCHAT,
                    matchedRule = DetectionRule(
                        MatchType.VIEW_ID, id,
                        appTarget = AppTarget.SNAPCHAT,
                        description = "Snapchat Spotlight view ID: $id"
                    )
                )
            }
        }

        // Strategy 2: Selected "Spotlight" tab
        if (NodeScanner.findSelectedNodeWithText(rootNode, SPOTLIGHT_TAB_PATTERN)) {
            return DetectionResult(
                detected = true,
                appTarget = AppTarget.SNAPCHAT,
                matchedRule = DetectionRule.HEURISTIC_SHORTS_TAB,
                confidence = 0.9f
            )
        }

        // Strategy 3: Content description matching + vertical video feed
        if (NodeScanner.findByContentDescription(rootNode, SPOTLIGHT_CONTENT_PATTERN)) {
            if (NodeScanner.hasVerticalPager(rootNode) && NodeScanner.hasVideoSurface(rootNode)) {
                return DetectionResult(
                    detected = true,
                    appTarget = AppTarget.SNAPCHAT,
                    matchedRule = DetectionRule.HEURISTIC_CONTENT_DESC,
                    confidence = 0.7f
                )
            }
        }

        // Strategy 4: Fallback — vertical pager + video surface (generic short-form pattern)
        if (NodeScanner.hasVerticalPager(rootNode) && NodeScanner.hasVideoSurface(rootNode)) {
            return DetectionResult(
                detected = true,
                appTarget = AppTarget.SNAPCHAT,
                matchedRule = DetectionRule.HEURISTIC_VERTICAL_VIDEO,
                confidence = 0.6f
            )
        }

        return DetectionResult.NOT_DETECTED
    }
}
