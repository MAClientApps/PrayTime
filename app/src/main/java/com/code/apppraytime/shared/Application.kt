package com.code.apppraytime.shared

import android.content.Context
import androidx.preference.PreferenceManager

class Application (private val context: Context) {

    companion object {
        private const val DATA_NAME = "APPLICATION_DATA"
        private const val FIRST_LAUNCH = "FIRST_LAUNCH"
        private const val PRIMARY_COLOR = "PRIMARY_COLOR"
        private const val ICON_TYPE = "ICON_TYPE"
        private const val DARK_THEME = "DARK_THEME"
        private const val ARABIC_TYPE = "ARABIC_TYPE"
        private const val LAYOUT_STYLE = "LAYOUT_STYLE"
        private const val TRANSLATION = "TRANSLATION"
        private const val TRANSLITERATION = "TRANSLITERATION"
        private const val ARABIC_FONT_SIZE = "FONT_SIZE"
        private const val TRANSLITERATION_FONT_SIZE = "TRANSLITERATION_FONT_SIZE"
        private const val TRANSLATION_FONT_SIZE = "TRANSLATION_FONT_SIZE"
        private const val LANGUAGE = "LANGUAGE"
        private const val TAJWEED = "TAJWEED"

        private const val PLAYING = "PLAYING"
        private const val PLAYING_AYAT = "PLAYING_AYAT"
        private const val PLAYING_SURAH = "PLAYING_SURAH"

        // Translation
        const val BN_BAYAAN = "bn_bayaan"
        const val BN_FOZLUR = "bn_fozlur"
        const val BN_MUJIBUR = "bn_mujibur"
        const val BN_TAISIRUL = "bn_taisirul"
        const val EN_HALEEM = "en_haleem"
        const val EN_SAHIH = "en_sahih"
        const val HINDI_FAROOQ = "hindi_farooq"
        const val IN_BAHASA = "in_bahasa"
        const val TR_DIYANET = "tr_diyanet"
        const val UR_JUNAGARHI = "ur_junagarhi"

        //Color
        const val GREEN = 0
        const val BLUE = 1
        const val ORANGE = 2
    }

    var playingSurah: Int
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getInt(PLAYING_SURAH, 1)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putInt(PLAYING_SURAH, value)
            editor.apply()
        }

    var playingAyat: Int
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getInt(PLAYING_AYAT, 1)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putInt(PLAYING_AYAT, value)
            editor.apply()
        }

    var playing: Boolean
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getBoolean(PLAYING, false)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putBoolean(PLAYING, value)
            editor.apply()
        }

    var tajweed: Boolean
        get() {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                prefs.getBoolean(TAJWEED, true) else false
        }
        set(value) {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPref.edit()
            editor.putBoolean(TAJWEED, value)
            editor.apply()
        }

    var language: String
        get() {
            val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
//            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return prefs.getString(LANGUAGE, "en")!!
        }
        set(value) {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPref.edit()
            editor.putString(LANGUAGE, value)
            editor.apply()
        }

    var firstLaunch: Boolean
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getBoolean(FIRST_LAUNCH, true)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putBoolean(FIRST_LAUNCH, value)
            editor.apply()
        }

    var layoutStyle: Int
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getInt(LAYOUT_STYLE, 1)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putInt(LAYOUT_STYLE, value)
            editor.apply()
        }

    var darkTheme: Boolean
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getBoolean(DARK_THEME, false)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putBoolean(DARK_THEME, value)
            editor.apply()
        }

    var arabic: Boolean
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getBoolean(ARABIC_TYPE, true)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putBoolean(ARABIC_TYPE, value)
            editor.apply()
        }

    var transliteration: Boolean
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getBoolean(TRANSLITERATION, false)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putBoolean(TRANSLITERATION, value)
            editor.apply()
        }

    var translation: String
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getString(TRANSLATION, EN_SAHIH)!!
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putString(TRANSLATION, value)
            editor.apply()
        }

    var arabicFontSize: Float
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getFloat(ARABIC_FONT_SIZE, 28f)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putFloat(ARABIC_FONT_SIZE, value)
            editor.apply()
        }

    var transliterationFontSize: Float
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getFloat(TRANSLITERATION_FONT_SIZE, 18f)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putFloat(TRANSLITERATION_FONT_SIZE, value)
            editor.apply()
        }

    var translationFontSize: Float
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getFloat(TRANSLATION_FONT_SIZE, 18f)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putFloat(TRANSLATION_FONT_SIZE, value)
            editor.apply()
        }

    var primaryColor: Int
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getInt(PRIMARY_COLOR, GREEN)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putInt(PRIMARY_COLOR, value)
            editor.apply()
        }

    var vectorIcon: Boolean
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getBoolean(ICON_TYPE, true)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putBoolean(ICON_TYPE, value)
            editor.apply()
        }
}