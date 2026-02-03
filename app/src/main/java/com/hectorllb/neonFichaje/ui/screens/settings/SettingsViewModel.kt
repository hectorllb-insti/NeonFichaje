package com.hectorllb.neonFichaje.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hectorllb.neonFichaje.domain.model.UserConfig
import com.hectorllb.neonFichaje.domain.repository.TimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: TimeRepository
) : ViewModel() {

    val configState: StateFlow<UserConfig?> = repository.getUserConfig()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    fun updateWeeklyTarget(hours: Double) {
        viewModelScope.launch {
            val current = configState.value ?: UserConfig(40.0, true)
            repository.updateUserConfig(current.copy(weeklyTargetHours = hours))
        }
    }
}
