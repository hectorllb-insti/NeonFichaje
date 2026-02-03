package com.hectorllb.neonFichaje.domain.model

import java.time.Instant

data class DashboardStats(
    val completedTodaySeconds: Long, // Only finished sessions
    val completedWeekSeconds: Long,
    val weeklyTargetSeconds: Long,
    val isClockedIn: Boolean,
    val currentSessionStartTime: Instant? = null
)
