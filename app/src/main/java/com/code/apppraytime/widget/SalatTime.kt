package com.code.apppraytime.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.code.apppraytime.R
import com.code.apppraytime.repository.SalatRepository
import com.code.apppraytime.screen.layout.Home
import com.code.apppraytime.shared.SalatTimes
import com.code.apppraytime.shared.SavedLocation
import com.code.apppraytime.times.Coordinates
import com.code.apppraytime.times.Madhab
import com.code.apppraytime.times.PrayerTimes
import com.code.apppraytime.times.data.DateComponents
import java.text.SimpleDateFormat
import java.util.*

class SalatTime : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val date = DateComponents.from(Date())
            val coordinates = Coordinates(
                SavedLocation(context).latitude,
                SavedLocation(context).longitude
            )
            val params = SalatTimes(context).getCalculation()
            params.madhab = if (SalatTimes(context).hanafi)
                Madhab.HANAFI else Madhab.SHAFI

            val prayerTimes = PrayerTimes(coordinates, date, params)

            val views = RemoteViews(context.packageName, R.layout.salat_time)

            views.setTextViewText(R.id.fajar,
                setTime(prayerTimes.fajr!!.time)
            )
            views.setTextViewText(R.id.dhr,
                setTime(prayerTimes.dhuhr!!.time)
            )
            views.setTextViewText(R.id.asr,
                setTime(prayerTimes.asr!!.time)
            )
            views.setTextViewText(R.id.maghrib,
                setTime(prayerTimes.maghrib!!.time)
            )
            views.setTextViewText(R.id.isha,
                setTime(prayerTimes.isha!!.time)
            )
            views.setTextViewText(R.id.city,
                SavedLocation(context).city
            )
            views.setTextViewText(R.id.country,
                SavedLocation(context).country
            )

            val intent = Intent().also {  it.action = "android.appwidget.action.APPWIDGET_UPDATE" }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )

            views.setOnClickPendingIntent(R.id.refresh, pendingIntent)

            Home().calculate(
                if (SalatRepository.salatNames.contains(prayerTimes.currentPrayer().name))
                    prayerTimes.currentPrayer().name else prayerTimes.nextPrayer().name,
                prayerTimes
            ).let {
                views.setTextViewText(R.id.salat_name, it.salat)
                views.setTextViewText(R.id.salat_time, it.time)
            }

            views.setTextViewText(
                R.id.last_update, "Last Update: " +
                SimpleDateFormat("hh:mm:ss a", Locale.US).format(Date())
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if ("android.appwidget.action.APPWIDGET_UPDATE" == intent?.action) {
            onUpdate(context)
        }
    }

    private fun onUpdate(context: Context?) {
        context?.let {
            onUpdate(
                it, AppWidgetManager.getInstance(context),
                AppWidgetManager.getInstance(context).getAppWidgetIds(
                    ComponentName(it.packageName, javaClass.name)
                )
            )
        }
    }

    fun setTime(time: Long): String {
        return SimpleDateFormat(
            Home.timePattern, Locale.getDefault()
        ).format(Date(time))
    }
}

