package com.scrollstop.app.detection.platform

import android.view.accessibility.AccessibilityNodeInfo
import com.scrollstop.app.detection.DetectionResult

interface PlatformDetector {
    val targetPackage: String
    fun detect(rootNode: AccessibilityNodeInfo): DetectionResult
}
