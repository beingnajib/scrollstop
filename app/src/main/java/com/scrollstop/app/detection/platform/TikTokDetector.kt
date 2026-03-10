package com.scrollstop.app.detection.platform

import android.view.accessibility.AccessibilityNodeInfo
import com.scrollstop.app.data.model.AppTarget
import com.scrollstop.app.detection.DetectionResult
import com.scrollstop.app.detection.DetectionRule
import com.scrollstop.app.detection.NodeScanner
import com.scrollstop.app.util.Constants

class TikTokDetector : PlatformDetector {

    override val targetPackage = Constants.PACKAGE_TIKTOK

    companion object {
        // Multi-language support for "For You" tab
        private val FOR_YOU_PATTERN = Regex("(?i)(for you|pour toi|para ti|fur dich|pour vous)")

        // Exclusion patterns — don't block these screens
        private val PROFILE_PATTERN = Regex("(?i)(profile|profil|perfil)")
        private val INBOX_PATTERN = Regex("(?i)(inbox|messages|nachricht)")
        private val SEARCH_PATTERN = Regex("(?i)(search|discover|recherch|buscar)")
    }

    override fun detect(rootNode: AccessibilityNodeInfo): DetectionResult {
        // Check if we're on an excluded screen first
        if (NodeScanner.findSelectedNodeWithText(rootNode, PROFILE_PATTERN) ||
            NodeScanner.findSelectedNodeWithText(rootNode, INBOX_PATTERN) ||
            NodeScanner.findSelectedNodeWithText(rootNode, SEARCH_PATTERN)
        ) {
            return DetectionResult.NOT_DETECTED
        }

        // Check for "For You" tab selection
        val isOnForYouTab = NodeScanner.findSelectedNodeWithText(rootNode, FOR_YOU_PATTERN)

        if (isOnForYouTab) {
            // Confirm video playback is active to avoid false positives on loading screens
            if (NodeScanner.hasVideoSurface(rootNode)) {
                return DetectionResult(
                    detected = true,
                    appTarget = AppTarget.TIKTOK,
                    matchedRule = DetectionRule.HEURISTIC_FOR_YOU_PAGE
                )
            }
        }

        // Fallback: check for vertical pager with video (TikTok's core pattern)
        if (NodeScanner.hasVerticalPager(rootNode) && NodeScanner.hasVideoSurface(rootNode)) {
            return DetectionResult(
                detected = true,
                appTarget = AppTarget.TIKTOK,
                matchedRule = DetectionRule.HEURISTIC_VERTICAL_VIDEO,
                confidence = 0.7f
            )
        }

        return DetectionResult.NOT_DETECTED
    }
}
