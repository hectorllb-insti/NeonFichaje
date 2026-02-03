package com.hectorllb.neonFichaje.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "default_schedule")
data class DefaultScheduleEntity(
    @PrimaryKey val dayOfWeek: Int, // 1 = Monday, 7 = Sunday
    val startTime: LocalTime,
    val endTime: LocalTime,
    val isEnabled: Boolean
)

@Entity(tableName = "daily_schedule")
data class DailyScheduleEntity(
    @PrimaryKey val date: LocalDate,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val isDayOff: Boolean
)
