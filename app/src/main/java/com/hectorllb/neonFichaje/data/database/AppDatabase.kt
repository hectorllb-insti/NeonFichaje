package com.hectorllb.neonFichaje.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hectorllb.neonFichaje.data.database.dao.TimeEntryDao
import com.hectorllb.neonFichaje.data.database.dao.UserConfigDao
import com.hectorllb.neonFichaje.data.database.entity.TimeEntryEntity
import com.hectorllb.neonFichaje.data.database.entity.UserConfigEntity

@Database(
    entities = [TimeEntryEntity::class, UserConfigEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun timeEntryDao(): TimeEntryDao
    abstract fun userConfigDao(): UserConfigDao
}
