package com.hectorllb.neonFichaje.domain.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

data class DefaultSchedule(
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val isEnabled: Boolean
)

data class DailySchedule(
    val date: LocalDate,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val isDayOff: Boolean
)
