package com.hectorllb.neonFichaje.data.repository

import com.hectorllb.neonFichaje.data.database.dao.TimeEntryDao
import com.hectorllb.neonFichaje.data.database.dao.UserConfigDao
import com.hectorllb.neonFichaje.data.database.entity.TimeEntryEntity
import com.hectorllb.neonFichaje.data.database.entity.UserConfigEntity
import com.hectorllb.neonFichaje.domain.model.TimeEntry
import com.hectorllb.neonFichaje.domain.model.UserConfig
import com.hectorllb.neonFichaje.domain.repository.TimeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class TimeRepositoryImpl @Inject constructor(
    private val timeEntryDao: TimeEntryDao,
    private val userConfigDao: UserConfigDao
) : TimeRepository {

    override suspend fun clockIn(entry: TimeEntry) {
        timeEntryDao.insert(entry.toEntity())
    }

    override suspend fun clockOut(entry: TimeEntry) {
        timeEntryDao.update(entry.toEntity())
    }

    override suspend fun updateEntry(entry: TimeEntry) {
        timeEntryDao.update(entry.toEntity())
    }

    override fun getOpenEntry(): Flow<TimeEntry?> {
        return timeEntryDao.getOpenEntryFlow().map { it?.toDomain() }
    }

    override suspend fun getOpenEntryOneShot(): TimeEntry? {
        return timeEntryDao.getOpenEntry()?.toDomain()
    }

    override fun getEntriesForDateRange(start: LocalDate, end: LocalDate): Flow<List<TimeEntry>> {
        return timeEntryDao.getEntriesBetweenDates(start, end).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getAllEntries(): Flow<List<TimeEntry>> {
        return timeEntryDao.getAllEntries().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getUserConfig(): Flow<UserConfig> {
        return userConfigDao.getConfig().map { 
            it?.toDomain() ?: UserConfig(40.0, true) // Default
        }
    }

    override suspend fun updateUserConfig(config: UserConfig) {
        userConfigDao.insertConfig(config.toEntity())
    }

    // Mappers
    private fun TimeEntry.toEntity() = TimeEntryEntity(
        id = id,
        startTime = startTime,
        endTime = endTime,
        date = date,
        notes = notes
    )

    private fun TimeEntryEntity.toDomain() = TimeEntry(
        id = id,
        startTime = startTime,
        endTime = endTime,
        date = date,
        notes = notes
    )

    private fun UserConfig.toEntity() = UserConfigEntity(
        weeklyTargetHours = weeklyTargetHours,
        flexibleSchedule = flexibleSchedule
    )

    private fun UserConfigEntity.toDomain() = UserConfig(
        weeklyTargetHours = weeklyTargetHours,
        flexibleSchedule = flexibleSchedule
    )
}
