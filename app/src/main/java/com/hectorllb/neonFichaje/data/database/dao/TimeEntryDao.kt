package com.hectorllb.neonFichaje.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hectorllb.neonFichaje.data.database.entity.TimeEntryEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TimeEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: TimeEntryEntity): Long

    @Update
    suspend fun update(entry: TimeEntryEntity)

    @Query("SELECT * FROM time_entries WHERE endTime IS NULL ORDER BY startTime DESC LIMIT 1")
    suspend fun getOpenEntry(): TimeEntryEntity?

    @Query("SELECT * FROM time_entries WHERE endTime IS NULL ORDER BY startTime DESC LIMIT 1")
    fun getOpenEntryFlow(): Flow<TimeEntryEntity?>

    @Query("SELECT * FROM time_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY startTime ASC")
    fun getEntriesBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<TimeEntryEntity>>

    @Query("SELECT * FROM time_entries ORDER BY startTime DESC")
    fun getAllEntries(): Flow<List<TimeEntryEntity>>
    
    @Query("SELECT * FROM time_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): TimeEntryEntity?
}
