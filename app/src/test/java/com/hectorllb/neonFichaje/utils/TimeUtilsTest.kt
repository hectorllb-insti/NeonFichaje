package com.hectorllb.neonFichaje.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeUtilsTest {

    @Test
    fun formatDuration_returnsCorrectFormat() {
        val seconds = 3600L + 1800L // 1h 30m
        val result = TimeUtils.formatDuration(seconds)
        assertEquals("1h 30m", result)
    }

    @Test
    fun formatDuration_zero() {
        val result = TimeUtils.formatDuration(0)
        assertEquals("0h 00m", result)
    }

    @Test
    fun formatDecimalHours_returnsCorrectFormat() {
        val result = TimeUtils.formatDecimalHours(5.5)
        assertEquals("5h 30m", result)
    }
}
