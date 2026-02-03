package com.hectorllb.neonFichaje.utils

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeUtils {
    private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM")

    fun formatDuration(seconds: Long): String {
        val duration = Duration.ofSeconds(seconds)
        val hours = duration.toHours()
        val minutes = duration.toMinutesPart()
        return String.format(Locale.getDefault(), "%dh %02dm", hours, minutes)
    }

    /**
     * Formats decimal hours (e.g. 5.5) to "5h 30m"
     */
    fun formatDecimalHours(hoursDecimal: Double): String {
        val totalSeconds = (hoursDecimal * 3600).toLong()
        return formatDuration(totalSeconds)
    }

    fun formatTime(instant: Instant): String {
        return TIME_FORMATTER
            .withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault())
            .format(instant)
    }
    
    fun formatDate(instant: Instant): String {
        return DATE_FORMATTER
            .withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault())
            .format(instant)
    }
}
