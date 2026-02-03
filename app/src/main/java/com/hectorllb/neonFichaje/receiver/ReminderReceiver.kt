package com.hectorllb.neonFichaje.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hectorllb.neonFichaje.utils.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {
    @Inject lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Recordatorio"
        val message = intent.getStringExtra("message") ?: ""
        notificationHelper.showReminderNotification(title, message)
    }
}
