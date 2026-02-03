package com.hectorllb.neonFichaje.domain.repository

import com.hectorllb.neonFichaje.domain.model.TimeEntry
import com.hectorllb.neonFichaje.domain.model.UserConfig
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TimeRepository {
    suspend fun clockIn(entry: TimeEntry)
    suspend fun clockOut(entry: TimeEntry)
    suspend fun updateEntry(entry: TimeEntry)
    
    fun getOpenEntry(): Flow<TimeEntry?>
    suspend fun getOpenEntryOneShot(): TimeEntry?
    
    fun getEntriesForDateRange(start: LocalDate, end: LocalDate): Flow<List<TimeEntry>>
    fun getAllEntries(): Flow<List<TimeEntry>>
    
    fun getUserConfig(): Flow<UserConfig>
    suspend fun updateUserConfig(config: UserConfig)
}
