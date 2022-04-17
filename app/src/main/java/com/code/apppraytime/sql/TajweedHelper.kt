package com.code.apppraytime.sql

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.code.apppraytime.BuildConfig
import com.code.apppraytime.model.TranslationModel

class TajweedHelper(context: Context, name: String) :
    SQLiteOpenHelper(context,
        "/data/data/${BuildConfig.APPLICATION_ID}" +
                "/quran/$name.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            ("CREATE TABLE "
                    + "tajweedQuran"
                    ) + " (" +
                    "id INTEGER PRIMARY KEY, " +
                    "surah INTEGER, " +
                    "ayah INTEGER, " +
                    "text TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("SQLite", "Database is readonly")
    }

    fun write(model: TranslationModel) {
        this.writableDatabase.run {
            val contentValues = ContentValues()
            contentValues.put("id", model.id)
            contentValues.put("surah", model.surah)
            contentValues.put("ayah", model.ayah)
            contentValues.put("text", model.text)
            insert("tajweedQuran", null, contentValues)
            close()
        }
    }
}