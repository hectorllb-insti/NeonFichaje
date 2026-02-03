package com.hectorllb.neonFichaje.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "time_entries")
data class TimeEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Instant,
    val endTime: Instant? = null,
    val date: LocalDate, // Useful for queries grouping by day
    val notes: String? = null
)
