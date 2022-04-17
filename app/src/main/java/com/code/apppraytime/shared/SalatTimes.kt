package com.code.apppraytime.shared

import android.content.Context
import com.code.apppraytime.times.CalculationMethod
import com.code.apppraytime.times.CalculationParameters

class SalatTimes(val context: Context) {

    companion object {
        private const val DATA_NAME = "SALAT_DATA"
        private const val MADHAB = "MADHAB"
        private const val CALCULATION = "CALCULATION"

//        val SHAFI = Madhab.SHAFI
//        val HANAFI = Madhab.HANAFI

        const val MUSLIM_WORLD_LEAGUE = 0
        const val EGYPTIAN = 1
        const val KARACHI = 2
        const val UMM_AL_QURA = 3
        const val DUBAI = 4
        const val MOON_SIGHTING_COMMITTEE = 5
        const val NORTH_AMERICA = 6
        const val KUWAIT = 7
        const val QATAR = 8
        const val SINGAPORE = 9
    }

    var hanafi: Boolean
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getBoolean(MADHAB, true)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putBoolean(MADHAB, value)
            editor.apply()
        }

    var calculation: Int
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getInt(CALCULATION, KARACHI)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putInt(CALCULATION, value)
            editor.apply()
        }


//    fun putCalculation(param: CalculationParameters) {
//        val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
//        val editor = sharedPref.edit()
//        editor.putInt(CALCULATION,
//            when(param) {
//                CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters -> MUSLIM_WORLD_LEAGUE
//                CalculationMethod.EGYPTIAN.parameters -> EGYPTIAN
//                CalculationMethod.KARACHI.parameters -> KARACHI
//                CalculationMethod.UMM_AL_QURA.parameters -> UMM_AL_QURA
//                CalculationMethod.DUBAI.parameters -> DUBAI
//                CalculationMethod.MOON_SIGHTING_COMMITTEE.parameters -> MOON_SIGHTING_COMMITTEE
//                CalculationMethod.NORTH_AMERICA.parameters -> NORTH_AMERICA
//                CalculationMethod.KUWAIT.parameters -> KUWAIT
//                CalculationMethod.QATAR.parameters -> QATAR
//                CalculationMethod.SINGAPORE.parameters -> SINGAPORE
//                else -> KARACHI
//            }
//        )
//        editor.apply()
//    }

    fun putCalculation(param: Int) {
        val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
        val editor = sharedPref.edit()
        editor.putInt(CALCULATION, param)
        editor.apply()
    }

    fun getCalculation(): CalculationParameters {
        return when(calculation) {
            MUSLIM_WORLD_LEAGUE -> CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters
            EGYPTIAN -> CalculationMethod.EGYPTIAN.parameters
            KARACHI -> CalculationMethod.KARACHI.parameters
            UMM_AL_QURA -> CalculationMethod.UMM_AL_QURA.parameters
            DUBAI -> CalculationMethod.DUBAI.parameters
            MOON_SIGHTING_COMMITTEE -> CalculationMethod.MOON_SIGHTING_COMMITTEE.parameters
            NORTH_AMERICA -> CalculationMethod.NORTH_AMERICA.parameters
            KUWAIT -> CalculationMethod.KUWAIT.parameters
            QATAR -> CalculationMethod.QATAR.parameters
            SINGAPORE -> CalculationMethod.SINGAPORE.parameters
            else -> CalculationMethod.KARACHI.parameters
        }
    }

    fun getCalculationText(): String {
        return when(calculation) {
            MUSLIM_WORLD_LEAGUE -> "Muslim World League (18°/17°)"
            EGYPTIAN -> "Egyptian General Authority (19.5°/17.5°)"
            KARACHI -> "Islamic University, Karachi (18.0°/18.0°)"
            UMM_AL_QURA -> "Umm Al-Qura University, Makkah (18.5°/90min)"
            DUBAI -> "The Gulf Region, Dubai (18.2°/18.2°)"
            MOON_SIGHTING_COMMITTEE -> "Moonsighting Committee (18°/18°)"
            NORTH_AMERICA -> "ISNA method (15°/15°) (not recommended)"
            KUWAIT -> "Kuwait (18°/17.5°)"
            QATAR -> "Qatar (18°/18°) (Modified version of Umm al-Qura)"
            SINGAPORE -> "Singapore (20°/18°)"
            else -> "Islamic University, Karachi (18.0°/18.0°)"
        }
    }

}