package com.hectorllb.neonFichaje.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hectorllb.neonFichaje.data.database.dao.ScheduleDao
import com.hectorllb.neonFichaje.data.database.dao.TimeEntryDao
import com.hectorllb.neonFichaje.data.database.dao.UserConfigDao
import com.hectorllb.neonFichaje.data.database.entity.DailyScheduleEntity
import com.hectorllb.neonFichaje.data.database.entity.DefaultScheduleEntity
import com.hectorllb.neonFichaje.data.database.entity.TimeEntryEntity
import com.hectorllb.neonFichaje.data.database.entity.UserConfigEntity

@Database(
    entities = [
        TimeEntryEntity::class,
        UserConfigEntity::class,
        DefaultScheduleEntity::class,
        DailyScheduleEntity::class
    ],
    version = 2, // Increment version
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun timeEntryDao(): TimeEntryDao
    abstract fun userConfigDao(): UserConfigDao
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `default_schedule` (`dayOfWeek` INTEGER NOT NULL, `startTime` TEXT NOT NULL, `endTime` TEXT NOT NULL, `isEnabled` INTEGER NOT NULL, PRIMARY KEY(`dayOfWeek`))")
                database.execSQL("CREATE TABLE IF NOT EXISTS `daily_schedule` (`date` TEXT NOT NULL, `startTime` TEXT, `endTime` TEXT, `isDayOff` INTEGER NOT NULL, PRIMARY KEY(`date`))")
            }
        }
    }
}
