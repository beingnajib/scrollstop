package com.scrollstop.app.detection

import com.scrollstop.app.data.model.AppTarget

data class DetectionRule(
    val matchType: MatchType,
    val targetValue: String,
    val appTarget: AppTarget = AppTarget.INSTAGRAM_REELS,
    val isHeuristic: Boolean = false,
    val description: String = ""
) {
    companion object {
        val NONE = DetectionRule(MatchType.HEURISTIC, "", description = "none")

        val HEURISTIC_VERTICAL_VIDEO = DetectionRule(
            MatchType.HEURISTIC, "vertical_video_pager",
            isHeuristic = true, description = "Vertical video pager heuristic"
        )
        val HEURISTIC_CONTENT_DESC = DetectionRule(
            MatchType.HEURISTIC, "content_description_match",
            isHeuristic = true, description = "Content description match"
        )
        val HEURISTIC_SHORTS_TAB = DetectionRule(
            MatchType.HEURISTIC, "shorts_tab_selected",
            isHeuristic = true, description = "Shorts tab selected"
        )
        val HEURISTIC_FOR_YOU_PAGE = DetectionRule(
            MatchType.HEURISTIC, "for_you_page",
            isHeuristic = true, description = "For You page detected"
        )
    }
}

enum class MatchType {
    VIEW_ID,
    CONTENT_DESCRIPTION,
    TEXT,
    CLASS_NAME,
    HEURISTIC
}
