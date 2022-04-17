package com.code.apppraytime.theme

import android.app.Activity
import com.code.apppraytime.R
import com.code.apppraytime.shared.Application

class ApplicationTheme(val context: Activity) {

    init {
        if (Application(context).darkTheme) {
            when(Application(context).primaryColor) {
                Application.GREEN -> context.setTheme(R.style.ThemeDarkGreen)
                Application.BLUE -> context.setTheme(R.style.ThemeDarkBlue)
                Application.ORANGE -> context.setTheme(R.style.ThemeDarkOrange)
            }
        } else {
            when(Application(context).primaryColor) {
                Application.GREEN -> context.setTheme(R.style.ThemeLightGreen)
                Application.BLUE -> context.setTheme(R.style.ThemeLightBlue)
                Application.ORANGE -> context.setTheme(R.style.ThemeLightOrange)
            }
        }
    }

    fun getAccentColor(): String {
        return when(Application(context).primaryColor) {
            Application.GREEN -> "#43a043"
            Application.BLUE -> "#2196F3"
            Application.ORANGE -> "#FF9800"
            else -> "red"
        }
    }

    fun getTextColor(): String {
        return if (Application(context).darkTheme)
            "#ffffff" else "#000000"
    }
}