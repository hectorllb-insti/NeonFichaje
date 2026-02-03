package com.hectorllb.neonFichaje.domain.model

import java.time.Instant
import java.time.LocalDate

data class TimeEntry(
    val id: Long = 0,
    val startTime: Instant,
    val endTime: Instant? = null,
    val date: LocalDate,
    val notes: String? = null
) {
    val durationSeconds: Long
        get() {
            val end = endTime ?: Instant.now()
            return java.time.Duration.between(startTime, end).seconds
        }
}
