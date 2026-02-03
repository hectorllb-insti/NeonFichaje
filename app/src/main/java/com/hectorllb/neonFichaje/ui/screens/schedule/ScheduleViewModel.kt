package com.hectorllb.neonFichaje.ui.screens.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hectorllb.neonFichaje.domain.model.DailySchedule
import com.hectorllb.neonFichaje.domain.model.DefaultSchedule
import com.hectorllb.neonFichaje.domain.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    // Default Schedules
    val defaultSchedules: StateFlow<List<DefaultSchedule>> = repository.getDefaultSchedules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calendar State
    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth

    private val _monthOverrides = MutableStateFlow<List<DailySchedule>>(emptyList())
    val monthOverrides: StateFlow<List<DailySchedule>> = _monthOverrides

    private val _selectedDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private var monthJob: Job? = null

    init {
        // Initialize defaults if empty
        viewModelScope.launch {
            val defaults = repository.getDefaultSchedules().first()
            if (defaults.isEmpty()) {
                val newDefaults = DayOfWeek.values().map { day ->
                    DefaultSchedule(
                        dayOfWeek = day,
                        startTime = LocalTime.of(9, 0),
                        endTime = LocalTime.of(17, 0),
                        isEnabled = day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY
                    )
                }
                newDefaults.forEach { repository.updateDefaultSchedule(it) }
            }
        }

        loadMonthData(YearMonth.now())
    }

    fun loadMonthData(yearMonth: YearMonth) {
        _currentMonth.value = yearMonth
        val start = yearMonth.atDay(1)
        val end = yearMonth.atEndOfMonth()

        monthJob?.cancel()
        monthJob = viewModelScope.launch {
            repository.getDailySchedulesInRange(start, end).collect {
                _monthOverrides.value = it
            }
        }
    }

    fun updateDefaultSchedule(schedule: DefaultSchedule) {
        viewModelScope.launch {
            repository.updateDefaultSchedule(schedule)
        }
    }

    fun updateDailySchedule(schedule: DailySchedule) {
        viewModelScope.launch {
            repository.updateDailySchedule(schedule)
        }
    }

    fun deleteDailySchedule(date: LocalDate) {
        viewModelScope.launch {
            repository.deleteDailySchedule(date)
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }
}
