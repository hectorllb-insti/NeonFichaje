package com.hectorllb.neonFichaje.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hectorllb.neonFichaje.data.database.entity.DailyScheduleEntity
import com.hectorllb.neonFichaje.data.database.entity.DefaultScheduleEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ScheduleDao {
    // Default Schedule
    @Query("SELECT * FROM default_schedule ORDER BY dayOfWeek ASC")
    fun getAllDefaultSchedules(): Flow<List<DefaultScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefaultSchedule(schedule: DefaultScheduleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefaultSchedules(schedules: List<DefaultScheduleEntity>)

    // Daily Overrides
    @Query("SELECT * FROM daily_schedule WHERE date = :date")
    fun getDailySchedule(date: LocalDate): Flow<DailyScheduleEntity?>

    @Query("SELECT * FROM daily_schedule WHERE date BETWEEN :startDate AND :endDate")
    fun getDailySchedulesInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailySchedule(schedule: DailyScheduleEntity)

    @Query("DELETE FROM daily_schedule WHERE date = :date")
    suspend fun deleteDailySchedule(date: LocalDate)
}
