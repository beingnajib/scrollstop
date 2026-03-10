package com.scrollstop.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val startTimeMinutes: Int,  // Minutes from midnight (e.g., 540 = 9:00 AM)
    val endTimeMinutes: Int,    // Minutes from midnight (e.g., 1020 = 5:00 PM)
    val daysOfWeek: Int,        // Bitmask: Mon=1, Tue=2, Wed=4, Thu=8, Fri=16, Sat=32, Sun=64
    val enabled: Boolean = true
)
