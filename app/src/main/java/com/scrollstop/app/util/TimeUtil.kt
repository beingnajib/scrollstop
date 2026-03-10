package com.scrollstop.app.util

import java.time.Duration
import java.time.LocalDate

object TimeUtil {

    fun formatDuration(duration: Duration): String {
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60

        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "0m"
        }
    }

    fun todayKey(): String = LocalDate.now().toString()
}
