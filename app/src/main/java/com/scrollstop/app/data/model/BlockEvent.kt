package com.scrollstop.app.data.model

data class BlockEvent(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val appTarget: AppTarget,
    val matchedRule: String,
    val dateKey: String
)
