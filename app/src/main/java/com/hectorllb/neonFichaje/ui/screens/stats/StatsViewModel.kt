package com.hectorllb.neonFichaje.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hectorllb.neonFichaje.domain.repository.TimeRepository
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    repository: TimeRepository
) : ViewModel() {

    // Simple weekly stats: Monday to Sunday of current week
    val chartEntryModelProducer = ChartEntryModelProducer()

    private val today = LocalDate.now()
    private val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    private val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

    val statsState: StateFlow<Unit> = repository.getEntriesForDateRange(startOfWeek, endOfWeek)
        .map { weekEntries ->
            // Group by DayOfWeek (1..7)
            val dailyHours = (1..7).map { dayIndex ->
                val dayOfWeek = DayOfWeek.of(dayIndex)
                val dayEntries = weekEntries.filter { it.date.dayOfWeek == dayOfWeek }
                val hours = dayEntries.sumOf { it.durationSeconds }.toFloat() / 3600f
                FloatEntry(x = dayIndex.toFloat(), y = hours)
            }
            
            chartEntryModelProducer.setEntries(dailyHours)
            Unit
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            Unit
        )
}
