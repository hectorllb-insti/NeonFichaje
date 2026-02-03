package com.hectorllb.neonFichaje.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.ZoneId

class TimeUtilsBenchmarkTest {

    @Test
    fun verifyFormats() {
        // Use a fixed instant for verification
        // 2023-10-05T14:30:00Z
        val instant = Instant.ofEpochSecond(1696516200)

        val timeString = TimeUtils.formatTime(instant)
        val dateString = TimeUtils.formatDate(instant)

        println("Sample Output Time: $timeString")
        println("Sample Output Date: $dateString")

        // Time should be HH:mm
        assert(timeString.matches(Regex("\\d{2}:\\d{2}")))

        // Date should be "EEE, dd MMM" e.g., "Thu, 05 Oct" or "jue., 05 oct."
        // We allow unicode letters (\p{L}) and dots, as some locales use abbreviations with dots.
        assert(dateString.matches(Regex("^[\\p{L}.]+, \\d{2} [\\p{L}.]+$")))
    }

    @Test
    fun benchmarkTimeUtils() {
        val iterations = 100000
        val instant = Instant.now()

        // Warmup
        for (i in 0 until 1000) {
            TimeUtils.formatTime(instant)
            TimeUtils.formatDate(instant)
        }

        // Benchmark formatTime
        val startTime = System.nanoTime()
        for (i in 0 until iterations) {
            TimeUtils.formatTime(instant)
        }
        val endTime = System.nanoTime()
        val timeDuration = (endTime - startTime) / 1_000_000.0 // ms

        // Benchmark formatDate
        val startDate = System.nanoTime()
        for (i in 0 until iterations) {
            TimeUtils.formatDate(instant)
        }
        val endDate = System.nanoTime()
        val dateDuration = (endDate - startDate) / 1_000_000.0 // ms

        println("Benchmark Results ($iterations iterations):")
        println("formatTime: ${timeDuration} ms")
        println("formatDate: ${dateDuration} ms")
        println("Total: ${timeDuration + dateDuration} ms")
    }
}
