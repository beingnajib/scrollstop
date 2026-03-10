package com.scrollstop.app.data

import com.scrollstop.app.data.local.dao.BlockEventDao
import com.scrollstop.app.data.local.dao.DailyStatProjection
import com.scrollstop.app.data.local.entity.BlockEventEntity
import com.scrollstop.app.detection.DetectionResult
import com.scrollstop.app.util.Constants
import com.scrollstop.app.util.TimeUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Duration

class StatsRepository(private val blockEventDao: BlockEventDao) {

    suspend fun recordBlock(result: DetectionResult) {
        blockEventDao.insert(
            BlockEventEntity(
                appTarget = result.appTarget.name,
                matchedRule = result.matchedRule.description,
                dateKey = TimeUtil.todayKey()
            )
        )
    }

    fun getTodayBlockCount(): Flow<Int> {
        return blockEventDao.getBlockCountForDate(TimeUtil.todayKey())
    }

    fun getTotalBlockCount(): Flow<Int> {
        return blockEventDao.getTotalBlockCount()
    }

    fun getWeeklyStats(): Flow<List<DailyStatProjection>> {
        return blockEventDao.getDailyStats(7)
    }

    fun getEstimatedTimeSaved(): Flow<String> {
        return getTotalBlockCount().map { count ->
            val duration = Duration.ofMinutes(count.toLong() * Constants.ESTIMATED_SESSION_MINUTES)
            TimeUtil.formatDuration(duration)
        }
    }
}
