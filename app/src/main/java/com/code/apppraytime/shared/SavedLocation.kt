package com.code.apppraytime.shared

import android.content.Context

class SavedLocation (private val context: Context) {

    companion object {
        private const val DATA_NAME = "APPLICATION_DATA"
        private const val LONGITUDE = "LONGITUDE"
        private const val LATITUDE = "LATITUDE"
        private const val COUNTRY = "COUNTRY"
        private const val CITY = "CITY"
    }

    var latitude: Double
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getString(LATITUDE, "0")!!.toDouble()
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putString(LATITUDE, value.toString())
            editor.apply()
        }

    var longitude: Double
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getString(LONGITUDE, "0")!!.toDouble()
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putString(LONGITUDE, value.toString())
            editor.apply()
        }

    var country: String
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getString(COUNTRY, "")!!
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putString(COUNTRY, value)
            editor.apply()
        }

    var city: String
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getString(CITY, "")!!
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putString(CITY, value)
            editor.apply()
        }
}