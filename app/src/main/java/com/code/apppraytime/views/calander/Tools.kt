package com.code.apppraytime.views.calander

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object Tools {
    @JvmStatic
    fun getTimeInMillis(date: String): Long {
        val c = Calendar.getInstance()
        c[date.split("-").toTypedArray()[2].toInt(), date.split("-")
            .toTypedArray()[1].toInt() - 1] =
            date.split("-").toTypedArray()[0].toInt()
        c[Calendar.HOUR_OF_DAY] = 0
        c[Calendar.MINUTE] = 1
        c[Calendar.SECOND] = 1
        c[Calendar.MILLISECOND] = 0
        return c.timeInMillis
    }

    @JvmStatic
    val formattedDateToday: String
        @SuppressLint("SimpleDateFormat")
        get() {
            val c = Calendar.getInstance()
            val sdf = SimpleDateFormat("dd-MM-yyyy")
            return sdf.format(c.timeInMillis)
        }
}