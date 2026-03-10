package com.scrollstop.app.data

import com.scrollstop.app.data.local.dao.ScheduleDao
import com.scrollstop.app.data.local.entity.ScheduleEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime

class ScheduleRepository(private val scheduleDao: ScheduleDao) {

    fun getAllSchedules(): Flow<List<ScheduleEntity>> {
        return scheduleDao.getAllSchedules()
    }

    suspend fun addSchedule(schedule: ScheduleEntity) {
        scheduleDao.insert(schedule)
    }

    suspend fun updateSchedule(schedule: ScheduleEntity) {
        scheduleDao.update(schedule)
    }

    suspend fun deleteSchedule(schedule: ScheduleEntity) {
        scheduleDao.delete(schedule)
    }

    suspend fun isBlockingActiveNow(): Boolean {
        val now = LocalTime.now()
        val today = LocalDate.now().dayOfWeek
        val todayBit = 1 shl (today.value - 1) // Mon=1, Tue=2, ...
        val nowMinutes = now.hour * 60 + now.minute

        val activeSchedules = scheduleDao.getEnabledSchedulesSync()
        if (activeSchedules.isEmpty()) return true // No schedule = always block

        return activeSchedules.any { schedule ->
            (schedule.daysOfWeek and todayBit) != 0 &&
                nowMinutes >= schedule.startTimeMinutes &&
                nowMinutes <= schedule.endTimeMinutes
        }
    }
}
