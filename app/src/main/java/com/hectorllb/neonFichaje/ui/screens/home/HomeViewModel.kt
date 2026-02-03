package com.hectorllb.neonFichaje.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hectorllb.neonFichaje.domain.model.DashboardStats
import com.hectorllb.neonFichaje.domain.usecase.ClockInUseCase
import com.hectorllb.neonFichaje.domain.usecase.ClockOutUseCase
import com.hectorllb.neonFichaje.domain.usecase.GetDashboardStatsUseCase
import com.hectorllb.neonFichaje.utils.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    // Combine stats with a ticker to update active duration
    // Actually, GetDashboardStatsUseCase depends on DB updates. 
    // To show "seconds ticking" we might need a local ticker in UI or here.
    // Ideally, the stats flow emits when DB changes. The ticker is purely for UI "elapsed time" display.
    // But "DashboardStats.workedTodaySeconds" includes the open session duration. 
    // If we want it to tick, we need to trigger the flow or combine with a ticker.
    
    // For simplicity, we will trust the UseCase updates on DB change, 
    // and we can have a separate "currentTime" flow for the UI to calculate live diffs if needed.
    // OR we trigger a refresh.
    
    // Let's rely on the UseCase. To make it "tick", we can emit into a trigger flow every second?
    // No, that's expensive for DB queries.
    // Better: The UI receives the "StartTime" of the active session. The UI counts up.
    // The "Stats" show the committed/base time.
    
    val uiState: StateFlow<HomeUiState> = getDashboardStatsUseCase()
        .map { stats -> HomeUiState(stats = stats, isLoading = false) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            HomeUiState()
        )

    fun onClockIn() {
        viewModelScope.launch {
            clockInUseCase().onSuccess {
                notificationHelper.showClockInNotification()
            }.onFailure { e ->
                // Handle error
            }
        }
    }

    fun onClockOut() {
        viewModelScope.launch {
            clockOutUseCase().onSuccess {
                notificationHelper.showClockOutNotification()
            }.onFailure { e ->
                // Handle error
            }
        }
    }
}

// Helper to map flow
import kotlinx.coroutines.flow.map
