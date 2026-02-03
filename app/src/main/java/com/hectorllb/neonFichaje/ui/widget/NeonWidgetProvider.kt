package com.hectorllb.neonFichaje.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.view.View
import android.widget.RemoteViews
import com.hectorllb.neonFichaje.MainActivity
import com.hectorllb.neonFichaje.R
import com.hectorllb.neonFichaje.domain.usecase.ClockInUseCase
import com.hectorllb.neonFichaje.domain.usecase.ClockOutUseCase
import com.hectorllb.neonFichaje.domain.usecase.GetDashboardStatsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NeonWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var getDashboardStatsUseCase: GetDashboardStatsUseCase

    @Inject
    lateinit var clockInUseCase: ClockInUseCase

    @Inject
    lateinit var clockOutUseCase: ClockOutUseCase

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Use goAsync ONCE per broadcast
        val pendingResult = goAsync()
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            try {
                for (appWidgetId in appWidgetIds) {
                    updateWidgetUI(context, appWidgetManager, appWidgetId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == ACTION_TOGGLE_CLOCK) {
            val pendingResult = goAsync()
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                try {
                    val stats = getDashboardStatsUseCase().first()
                    if (stats.isClockedIn) {
                        clockOutUseCase()
                    } else {
                        clockInUseCase()
                    }

                    // Force Update All Widgets
                    val manager = AppWidgetManager.getInstance(context)
                    val thisWidget = ComponentName(context, NeonWidgetProvider::class.java)
                    val allWidgetIds = manager.getAppWidgetIds(thisWidget)
                    for (id in allWidgetIds) {
                        updateWidgetUI(context, manager, id)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    private suspend fun updateWidgetUI(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        try {
            val stats = getDashboardStatsUseCase().first()

            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            if (stats.isClockedIn && stats.currentSessionStartTime != null) {
                val elapsedDuration = System.currentTimeMillis() - stats.currentSessionStartTime.toEpochMilli()
                val base = SystemClock.elapsedRealtime() - elapsedDuration

                views.setViewVisibility(R.id.widget_hours_text, View.GONE)
                views.setViewVisibility(R.id.widget_chronometer, View.VISIBLE)
                views.setChronometer(R.id.widget_chronometer, base, null, true)

                views.setViewVisibility(R.id.widget_btn_in, View.GONE)
                views.setViewVisibility(R.id.widget_btn_out, View.VISIBLE)

                val intent = Intent(context, NeonWidgetProvider::class.java).apply {
                    action = ACTION_TOGGLE_CLOCK
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_btn_out, pendingIntent)

            } else {
                val hours = stats.completedTodaySeconds / 3600
                val minutes = (stats.completedTodaySeconds % 3600) / 60
                val timeStr = String.format("%02dh %02dm", hours, minutes)

                views.setViewVisibility(R.id.widget_hours_text, View.VISIBLE)
                views.setViewVisibility(R.id.widget_chronometer, View.GONE)
                views.setTextViewText(R.id.widget_hours_text, timeStr)

                views.setViewVisibility(R.id.widget_btn_in, View.VISIBLE)
                views.setViewVisibility(R.id.widget_btn_out, View.GONE)

                 val intent = Intent(context, NeonWidgetProvider::class.java).apply {
                    action = ACTION_TOGGLE_CLOCK
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_btn_in, pendingIntent)
            }

            val appIntent = Intent(context, MainActivity::class.java)
            val appPendingIntent = PendingIntent.getActivity(
                context, 0, appIntent, PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_title, appPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val ACTION_TOGGLE_CLOCK = "com.hectorllb.neonFichaje.ACTION_TOGGLE_CLOCK"
    }
}
