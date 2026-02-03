package com.hectorllb.neonFichaje.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.hectorllb.neonFichaje.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_STATUS_ID = "neon_fichaje_status"
        const val CHANNEL_REMINDERS_ID = "neon_fichaje_reminders"

        const val NOTIFICATION_ID = 1001
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Status Channel (Low Importance, for ongoing timer)
            val statusChannel = NotificationChannel(
                CHANNEL_STATUS_ID,
                "Estado del Fichaje",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Muestra el tiempo transcurrido de la jornada actual"
                setShowBadge(false)
            }

            // Reminders Channel (High Importance, for alerts)
            val remindersChannel = NotificationChannel(
                CHANNEL_REMINDERS_ID,
                "Recordatorios",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Avisos de entrada y salida"
                enableVibration(true)
            }

            notificationManager.createNotificationChannels(listOf(statusChannel, remindersChannel))
        }
    }

    // Deprecated: Replaced by TimerService
    fun showClockInNotification() {
        // No-op or delegate to service logic
    }

    fun showClockOutNotification() {
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        
        // Cancel the ongoing timer notification
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)

        val builder = NotificationCompat.Builder(context, CHANNEL_STATUS_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("Has fichado salida")
            .setContentText("Tu jornada ha terminado.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID + 1, builder.build())
    }

    fun getTimerNotification(startTime: Long): android.app.Notification {
        createNotificationChannels()

        return NotificationCompat.Builder(context, CHANNEL_STATUS_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("Jornada en curso")
            .setWhen(startTime)
            .setUsesChronometer(true)
            .setOngoing(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    fun showReminderNotification(title: String, message: String) {
        createNotificationChannels()
        val builder = NotificationCompat.Builder(context, CHANNEL_REMINDERS_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
