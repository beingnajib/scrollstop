package com.scrollstop.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.scrollstop.app.data.local.entity.BlockEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockEventDao {

    @Insert
    suspend fun insert(event: BlockEventEntity)

    @Query("SELECT COUNT(*) FROM block_events WHERE dateKey = :date")
    fun getBlockCountForDate(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM block_events WHERE dateKey = :date AND appTarget = :target")
    fun getBlockCountForDateAndTarget(date: String, target: String): Flow<Int>

    @Query("SELECT dateKey, COUNT(*) as count FROM block_events GROUP BY dateKey ORDER BY dateKey DESC LIMIT :days")
    fun getDailyStats(days: Int = 30): Flow<List<DailyStatProjection>>

    @Query("SELECT COUNT(*) FROM block_events")
    fun getTotalBlockCount(): Flow<Int>

    @Query("DELETE FROM block_events WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long)
}

data class DailyStatProjection(val dateKey: String, val count: Int)
