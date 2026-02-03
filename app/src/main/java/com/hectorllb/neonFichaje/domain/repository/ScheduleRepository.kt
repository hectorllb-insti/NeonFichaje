package com.hectorllb.neonFichaje.domain.repository

import com.hectorllb.neonFichaje.domain.model.DailySchedule
import com.hectorllb.neonFichaje.domain.model.DefaultSchedule
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ScheduleRepository {
    fun getDefaultSchedules(): Flow<List<DefaultSchedule>>
    suspend fun updateDefaultSchedule(schedule: DefaultSchedule)

    fun getDailySchedule(date: LocalDate): Flow<DailySchedule?>
    fun getDailySchedulesInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailySchedule>>
    suspend fun updateDailySchedule(schedule: DailySchedule)
    suspend fun deleteDailySchedule(date: LocalDate)
}
