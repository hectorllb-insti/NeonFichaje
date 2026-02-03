package com.hectorllb.neonFichaje.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hectorllb.neonFichaje.domain.repository.ScheduleRepository
import com.hectorllb.neonFichaje.receiver.ReminderReceiver
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class SchedulerWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SchedulerWorkerEntryPoint {
        fun scheduleRepository(): ScheduleRepository
    }

    override suspend fun doWork(): Result {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            SchedulerWorkerEntryPoint::class.java
        )
        val repository = entryPoint.scheduleRepository()

        val today = LocalDate.now()
        val now = LocalDateTime.now()

        // Schedule Start Alarm
        scheduleNextEvent(repository, today, now, isStart = true, requestCode = 100, message = "Es hora de entrar")

        // Schedule End Alarm
        scheduleNextEvent(repository, today, now, isStart = false, requestCode = 101, message = "Es hora de salir")

        return Result.success()
    }

    private suspend fun scheduleNextEvent(
        repository: ScheduleRepository,
        today: LocalDate,
        now: LocalDateTime,
        isStart: Boolean,
        requestCode: Int,
        message: String
    ) {
        // Check today first
        val todayTime = getEffectiveTime(repository, today, isStart)
        if (todayTime != null) {
            val todayDateTime = todayTime.atDate(today)
            if (todayDateTime.isAfter(now)) {
                scheduleAlarm(todayDateTime, message, requestCode)
                return
            }
        }

        // If today passed or no schedule, check tomorrow
        val tomorrow = today.plusDays(1)
        val tomorrowTime = getEffectiveTime(repository, tomorrow, isStart)
        if (tomorrowTime != null) {
            scheduleAlarm(tomorrowTime.atDate(tomorrow), message, requestCode)
        }
    }

    private suspend fun getEffectiveTime(
        repository: ScheduleRepository,
        date: LocalDate,
        isStart: Boolean
    ): java.time.LocalTime? {
        val override = repository.getDailySchedule(date).first()

        if (override != null) {
            if (override.isDayOff) return null
            return if (isStart) override.startTime else override.endTime
        }

        val defaults = repository.getDefaultSchedules().first()
        val default = defaults.find { it.dayOfWeek == date.dayOfWeek }

        if (default != null && default.isEnabled) {
            return if (isStart) default.startTime else default.endTime
        }

        return null
    }

    private fun scheduleAlarm(dateTime: LocalDateTime, message: String, requestCode: Int) {
        if (dateTime.isBefore(LocalDateTime.now())) return

        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= 31 && !alarmManager.canScheduleExactAlarms()) {
            // Fallback or just ignore if permission missing (should handle better in real app)
            return
        }

        val intent = Intent(applicationContext, ReminderReceiver::class.java).apply {
            putExtra("title", "Recordatorio Neon Fichaje")
            putExtra("message", message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAtMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }
}
