package com.scrollstop.app.detection

import com.scrollstop.app.data.model.AppTarget

data class DetectionResult(
    val detected: Boolean,
    val appTarget: AppTarget = AppTarget.INSTAGRAM_REELS,
    val matchedRule: DetectionRule = DetectionRule.NONE,
    val confidence: Float = 1.0f
) {
    companion object {
        val NOT_DETECTED = DetectionResult(detected = false)
    }
}
