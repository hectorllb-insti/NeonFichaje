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

    val statsState: StateFlow<Unit> = repository.getAllEntries()
        .map { entries ->
            val today = LocalDate.now()
            val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

            val weekEntries = entries.filter {
                !it.date.isBefore(startOfWeek) && !it.date.isAfter(endOfWeek)
            }

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
