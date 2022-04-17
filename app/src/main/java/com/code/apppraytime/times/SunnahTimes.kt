package com.code.apppraytime.times

import com.code.apppraytime.times.data.CalendarUtil
import com.code.apppraytime.times.data.DateComponents
import java.util.*

class SunnahTimes(prayerTimes: PrayerTimes) {

    val middleOfTheNight: Date

    val lastThirdOfTheNight: Date

    init {
        val currentPrayerTimesDate = CalendarUtil.resolveTime(prayerTimes.dateComponents)
        val tomorrowPrayerTimesDate = CalendarUtil.add(currentPrayerTimesDate, 1, Calendar.DATE)
        val tomorrowPrayerTimes = PrayerTimes(
            prayerTimes.coordinates,
            DateComponents.fromUTC(tomorrowPrayerTimesDate),
            prayerTimes.calculationParameters
        )
        val nightDurationInSeconds =
            ((tomorrowPrayerTimes.fajr!!.time - prayerTimes.maghrib!!.time) / 1000).toInt()
        middleOfTheNight = CalendarUtil.roundedMinute(
            CalendarUtil.add(
                prayerTimes.maghrib,
                (nightDurationInSeconds / 2.0).toInt(),
                Calendar.SECOND
            )
        )
        lastThirdOfTheNight = CalendarUtil.roundedMinute(
            CalendarUtil.add(
                prayerTimes.maghrib,
                (nightDurationInSeconds * (2.0 / 3.0)).toInt(),
                Calendar.SECOND
            )
        )
    }
}