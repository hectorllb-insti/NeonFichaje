package com.hectorllb.neonFichaje.utils

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeUtils {
    fun formatDuration(seconds: Long): String {
        val duration = Duration.ofSeconds(seconds)
        val hours = duration.toHours()
        val minutes = duration.toMinutesPart()
        return String.format(Locale.getDefault(), "%02dh %02dm", hours, minutes)
    }

    fun formatTime(instant: Instant): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }
    
    fun formatDate(instant: Instant): String {
        val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM")
            .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }
}
