package com.hectorllb.neonFichaje.ui.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hectorllb.neonFichaje.domain.model.DailySchedule
import com.hectorllb.neonFichaje.domain.model.DefaultSchedule
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val titles = listOf("Plantilla Semanal", "Calendario")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTab) {
            0 -> DefaultScheduleView(viewModel)
            1 -> CalendarView(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultScheduleView(viewModel: ScheduleViewModel) {
    val schedules by viewModel.defaultSchedules.collectAsState()
    var showTimePicker by remember { mutableStateOf<Pair<DefaultSchedule, Boolean>?>(null) } // Schedule + isStart

    if (showTimePicker != null) {
        val (schedule, isStart) = showTimePicker!!
        val initialTime = if (isStart) schedule.startTime else schedule.endTime
        val pickerState = rememberTimePickerState(
            initialHour = initialTime.hour,
            initialMinute = initialTime.minute
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = null },
            confirmButton = {
                TextButton(onClick = {
                    val newTime = LocalTime.of(pickerState.hour, pickerState.minute)
                    val newSchedule = if (isStart) schedule.copy(startTime = newTime) else schedule.copy(endTime = newTime)
                    viewModel.updateDefaultSchedule(newSchedule)
                    showTimePicker = null
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = null }) { Text("Cancelar") }
            },
            text = { TimePicker(state = pickerState) }
        )
    }

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(schedules) { schedule ->
            Card {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = schedule.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                            .replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Switch(
                            checked = schedule.isEnabled,
                            onCheckedChange = { viewModel.updateDefaultSchedule(schedule.copy(isEnabled = it)) }
                        )
                        if (schedule.isEnabled) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = schedule.startTime.toString(),
                                    modifier = Modifier.clickable { showTimePicker = schedule to true },
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(" - ")
                                Text(
                                    text = schedule.endTime.toString(),
                                    modifier = Modifier.clickable { showTimePicker = schedule to false },
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            Text("Descanso", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarView(viewModel: ScheduleViewModel) {
    val currentMonth by viewModel.currentMonth.collectAsState()
    val overrides by viewModel.monthOverrides.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    var showEditor by remember { mutableStateOf<LocalDate?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Month Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.loadMonthData(currentMonth.minusMonths(1)) }) {
                Icon(Icons.Default.ArrowBack, "Anterior")
            }
            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}".uppercase(),
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = { viewModel.loadMonthData(currentMonth.plusMonths(1)) }) {
                Icon(Icons.Default.ArrowForward, "Siguiente")
            }
        }

        // Calendar Grid
        val daysInMonth = currentMonth.lengthOfMonth()
        val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value // 1=Mon, 7=Sun

        // Days header
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("L", "M", "X", "J", "V", "S", "D").forEach {
                Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.weight(1f)
        ) {
            // Empty cells for offset
            items(firstDayOfWeek - 1) { Spacer(Modifier) }

            items(daysInMonth) { index ->
                val date = currentMonth.atDay(index + 1)
                val override = overrides.find { it.date == date }

                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (date == selectedDate) MaterialTheme.colorScheme.primaryContainer
                            else if (override != null) MaterialTheme.colorScheme.secondaryContainer
                            else Color.Transparent
                        )
                        .clickable {
                            viewModel.selectDate(date)
                            showEditor = date
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = (index + 1).toString(),
                            color = if (date == selectedDate) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                        if (override != null) {
                            Box(modifier = Modifier.width(4.dp).height(4.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                        }
                    }
                }
            }
        }
    }

    if (showEditor != null) {
        val date = showEditor!!
        val existingOverride = overrides.find { it.date == date }

        var isDayOff by remember { mutableStateOf(existingOverride?.isDayOff ?: false) }
        var start by remember { mutableStateOf(existingOverride?.startTime ?: LocalTime.of(9, 0)) }
        var end by remember { mutableStateOf(existingOverride?.endTime ?: LocalTime.of(17, 0)) }

        AlertDialog(
            onDismissRequest = { showEditor = null },
            title = { Text("Editar ${date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())} $date") },
            text = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Día libre")
                        Switch(checked = isDayOff, onCheckedChange = { isDayOff = it })
                    }
                    if (!isDayOff) {
                        // Ideally TimePickers here, simplified for brevity:
                        // Just showing text for now, user can reset to default
                        Text("Horario Personalizado:")
                         // TODO: Add TimePickers for Override
                         // For now I'll just save what we have or delete
                         Text("${start} - ${end}")
                         Text("Nota: Edición de hora detallada pendiente de implementar en diálogo.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateDailySchedule(DailySchedule(date, if(isDayOff) null else start, if(isDayOff) null else end, isDayOff))
                    showEditor = null
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.deleteDailySchedule(date)
                    showEditor = null
                }) { Text("Restaurar Default") }
            }
        )
    }
}
