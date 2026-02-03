package com.hectorllb.neonFichaje.domain.usecase

import com.hectorllb.neonFichaje.domain.model.DashboardStats
import com.hectorllb.neonFichaje.domain.repository.TimeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class GetDashboardStatsUseCase @Inject constructor(
    private val repository: TimeRepository
) {
    operator fun invoke(): Flow<DashboardStats> {
        return combine(
            repository.getAllEntries(),
            repository.getUserConfig(),
            repository.getOpenEntry()
        ) { entries, config, openEntry ->
            
            val today = LocalDate.now()
            val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

            // Calculate Closed Sessions only
            val weekEntries = entries.filter { 
                !it.date.isBefore(startOfWeek) && !it.date.isAfter(endOfWeek) && it.endTime != null
            }
            
            val todayEntries = entries.filter {
                it.date.isEqual(today) && it.endTime != null
            }

            val completedWeekSeconds = weekEntries.sumOf { it.durationSeconds }
            val completedTodaySeconds = todayEntries.sumOf { it.durationSeconds }
            val targetSeconds = (config.weeklyTargetHours * 3600).toLong()

            DashboardStats(
                completedTodaySeconds = completedTodaySeconds,
                completedWeekSeconds = completedWeekSeconds,
                weeklyTargetSeconds = targetSeconds,
                isClockedIn = openEntry != null,
                currentSessionStartTime = openEntry?.startTime
            )
        }
    }
}
