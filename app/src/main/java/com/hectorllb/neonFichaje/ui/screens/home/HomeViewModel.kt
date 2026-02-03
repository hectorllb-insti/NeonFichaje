package com.hectorllb.neonFichaje.ui.screens.home

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hectorllb.neonFichaje.domain.model.DashboardStats
import com.hectorllb.neonFichaje.domain.usecase.ClockInUseCase
import com.hectorllb.neonFichaje.domain.usecase.ClockOutUseCase
import com.hectorllb.neonFichaje.domain.usecase.GetDashboardStatsUseCase
import com.hectorllb.neonFichaje.service.TimerService
import com.hectorllb.neonFichaje.utils.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class HomeUiState(
    val stats: DashboardStats? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val clockInUseCase: ClockInUseCase,
    private val clockOutUseCase: ClockOutUseCase,
    getDashboardStatsUseCase: GetDashboardStatsUseCase,
    private val notificationHelper: NotificationHelper,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    val uiState: StateFlow<HomeUiState> = getDashboardStatsUseCase()
        .map { stats ->
            // Check if we need to restore service (e.g. after app kill)
            if (stats.isClockedIn) {
                // Ensure service is running
                startTimerService(stats.currentSessionStartTime?.toEpochMilli() ?: 0L)
            }
            HomeUiState(stats = stats, isLoading = false)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            HomeUiState()
        )

    fun onClockIn() {
        viewModelScope.launch {
            clockInUseCase().onSuccess {
                // Service will be started by the Flow update, but we can start it immediately for responsiveness
                // We assume start time is approx now
                startTimerService(System.currentTimeMillis())
            }.onFailure { e ->
                // Handle error
            }
        }
    }

    fun onClockOut() {
        viewModelScope.launch {
            clockOutUseCase().onSuccess {
                notificationHelper.showClockOutNotification()
                // Service will stop itself when it detects entry closed
            }.onFailure { e ->
                // Handle error
            }
        }
    }

    private fun startTimerService(startTime: Long) {
        val intent = Intent(context, TimerService::class.java).apply {
            putExtra(TimerService.EXTRA_START_TIME, startTime)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
}
