package com.code.apppraytime.shared

import android.content.Context

class SystemData(private val context: Context) {

    companion object {
        private const val DATA_NAME = "SYSTEM_DATA"
        private const val HOME_SCROLL_POSITION_X = "HOME_SCROLL_POSITION_X"
        private const val SETTINGS_SCROLL_POSITION_X = "SETTINGS_SCROLL_POSITION_X"
    }

    var homeScroll: Int
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getInt(HOME_SCROLL_POSITION_X, 0)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putInt(HOME_SCROLL_POSITION_X, value)
            editor.apply()
        }

    var settingsScroll: Int
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getInt(SETTINGS_SCROLL_POSITION_X, 0)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putInt(SETTINGS_SCROLL_POSITION_X, value)
            editor.apply()
        }
}