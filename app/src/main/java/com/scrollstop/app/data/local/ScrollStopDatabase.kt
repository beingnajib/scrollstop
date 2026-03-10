package com.scrollstop.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.scrollstop.app.data.local.dao.BlockEventDao
import com.scrollstop.app.data.local.dao.ScheduleDao
import com.scrollstop.app.data.local.entity.BlockEventEntity
import com.scrollstop.app.data.local.entity.ScheduleEntity

@Database(
    entities = [BlockEventEntity::class, ScheduleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ScrollStopDatabase : RoomDatabase() {
    abstract fun blockEventDao(): BlockEventDao
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: ScrollStopDatabase? = null

        fun getInstance(context: Context): ScrollStopDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    ScrollStopDatabase::class.java,
                    "scrollstop.db"
                )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
