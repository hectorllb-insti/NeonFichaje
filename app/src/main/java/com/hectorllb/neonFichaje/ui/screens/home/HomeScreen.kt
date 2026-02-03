package com.hectorllb.neonFichaje.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hectorllb.neonFichaje.utils.TimeUtils
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Ticker state
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            now = System.currentTimeMillis()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hola, Héctor!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            val stats = uiState.stats
            if (stats != null) {
                val currentSessionDuration = if (stats.isClockedIn && stats.currentSessionStartTime != null) {
                    Duration.between(stats.currentSessionStartTime, Instant.ofEpochMilli(now)).seconds
                } else {
                    0L
                }

                val totalWeekSeconds = stats.completedWeekSeconds + currentSessionDuration
                val totalTodaySeconds = stats.completedTodaySeconds + currentSessionDuration
                
                val progress = if (stats.weeklyTargetSeconds > 0) {
                    totalWeekSeconds.toFloat() / stats.weeklyTargetSeconds
                } else 0f

                // Progress Circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(250.dp)
                ) {
                    val progressColor = MaterialTheme.colorScheme.tertiary
                    val trackColor = MaterialTheme.colorScheme.surfaceVariant

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            color = trackColor,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 20.dp.toPx())
                        )
                        drawArc(
                            color = progressColor,
                            startAngle = -90f,
                            sweepAngle = progress * 360f,
                            useCenter = false,
                            style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Semana",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Stats Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatCard(
                        title = "Hoy",
                        value = TimeUtils.formatDuration(totalTodaySeconds),
                        color = MaterialTheme.colorScheme.primary
                    )
                    StatCard(
                        title = "Objetivo",
                        value = TimeUtils.formatDuration(stats.weeklyTargetSeconds),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Main Button
                Button(
                    onClick = {
                        if (stats.isClockedIn) viewModel.onClockOut() else viewModel.onClockIn()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (stats.isClockedIn) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text(
                        text = if (stats.isClockedIn) "SALIR" else "ENTRAR",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (stats.isClockedIn) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onTertiary
                    )
                }
                
                if (stats.isClockedIn) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tiempo actual: ${TimeUtils.formatDuration(currentSessionDuration)}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, color: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.size(140.dp, 100.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
