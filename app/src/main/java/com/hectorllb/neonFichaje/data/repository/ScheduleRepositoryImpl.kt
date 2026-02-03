package com.hectorllb.neonFichaje.data.repository

import com.hectorllb.neonFichaje.data.database.dao.ScheduleDao
import com.hectorllb.neonFichaje.data.database.entity.DailyScheduleEntity
import com.hectorllb.neonFichaje.data.database.entity.DefaultScheduleEntity
import com.hectorllb.neonFichaje.domain.model.DailySchedule
import com.hectorllb.neonFichaje.domain.model.DefaultSchedule
import com.hectorllb.neonFichaje.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val dao: ScheduleDao
) : ScheduleRepository {

    override fun getDefaultSchedules(): Flow<List<DefaultSchedule>> {
        return dao.getAllDefaultSchedules().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun updateDefaultSchedule(schedule: DefaultSchedule) {
        dao.insertDefaultSchedule(schedule.toEntity())
    }

    override fun getDailySchedule(date: LocalDate): Flow<DailySchedule?> {
        return dao.getDailySchedule(date).map { it?.toDomain() }
    }

    override fun getDailySchedulesInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailySchedule>> {
        return dao.getDailySchedulesInRange(startDate, endDate).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun updateDailySchedule(schedule: DailySchedule) {
        dao.insertDailySchedule(schedule.toEntity())
    }

    override suspend fun deleteDailySchedule(date: LocalDate) {
        dao.deleteDailySchedule(date)
    }

    private fun DefaultScheduleEntity.toDomain() = DefaultSchedule(
        dayOfWeek = DayOfWeek.of(dayOfWeek),
        startTime = startTime,
        endTime = endTime,
        isEnabled = isEnabled
    )

    private fun DefaultSchedule.toEntity() = DefaultScheduleEntity(
        dayOfWeek = dayOfWeek.value,
        startTime = startTime,
        endTime = endTime,
        isEnabled = isEnabled
    )

    private fun DailyScheduleEntity.toDomain() = DailySchedule(
        date = date,
        startTime = startTime,
        endTime = endTime,
        isDayOff = isDayOff
    )

    private fun DailySchedule.toEntity() = DailyScheduleEntity(
        date = date,
        startTime = startTime,
        endTime = endTime,
        isDayOff = isDayOff
    )
}
