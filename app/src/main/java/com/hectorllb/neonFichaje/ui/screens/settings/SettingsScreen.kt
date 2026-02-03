package com.hectorllb.neonFichaje.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hectorllb.neonFichaje.utils.TimeUtils
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val config by viewModel.configState.collectAsState()

    // Local state for hours/minutes
    var hoursStr by remember { mutableStateOf("") }
    var minutesStr by remember { mutableStateOf("") }

    // Initialize state when config loads (only if not already set or external change)
    LaunchedEffect(config) {
        if (config != null) {
            val totalHours = config!!.weeklyTargetHours
            val h = totalHours.toInt()
            val m = ((totalHours - h) * 60).roundToInt()

            val currentH = hoursStr.toIntOrNull() ?: 0
            val currentM = minutesStr.toIntOrNull() ?: 0

            // Update local state only if it differs significantly (logic change)
            // or if it's the first load (empty strings)
            if ((h != currentH || m != currentM) || (hoursStr.isEmpty() && minutesStr.isEmpty())) {
                hoursStr = h.toString()
                minutesStr = m.toString()
            }
        }
    }

    // Function to save changes
    fun saveChanges(hStr: String, mStr: String) {
        val h = hStr.toIntOrNull() ?: 0
        val m = mStr.toIntOrNull() ?: 0
        val total = h + (m / 60.0)
        viewModel.updateWeeklyTarget(total)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Ajustes",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Objetivo Semanal",
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = hoursStr,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        hoursStr = it
                        saveChanges(it, minutesStr)
                    }
                },
                label = { Text("Horas") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            Text(
                text = ":",
                style = MaterialTheme.typography.headlineMedium
            )

            OutlinedTextField(
                value = minutesStr,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        minutesStr = it
                        saveChanges(hoursStr, it)
                    }
                },
                label = { Text("Minutos") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Total: ${TimeUtils.formatDecimalHours(config?.weeklyTargetHours ?: 0.0)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Horario Flexible activado por defecto.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
