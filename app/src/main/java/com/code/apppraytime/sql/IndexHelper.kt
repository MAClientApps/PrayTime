package com.code.apppraytime.sql

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.code.apppraytime.BuildConfig
import com.code.apppraytime.model.JuzModel
import com.code.apppraytime.model.Surah
import javax.inject.Singleton

@SuppressLint("SdCardPath")
@Singleton
class IndexHelper(context: Context) :
    SQLiteOpenHelper(context,
        "/data/data/${BuildConfig.APPLICATION_ID}" +
                "/quran/index.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d("SQLite", "Database is readonly")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("SQLite", "Database is readonly")
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        db?.disableWriteAheadLogging()
    }

    fun readSurahAll(): ArrayList<Surah> {
        val data =ArrayList<Surah>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM surah"
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    Surah(
                        id = cursor.getInt(0),
                        name = cursor.getString(1),
                        arabic = cursor.getString(2),
                        revelation = cursor.getString(3),
                        verse = cursor.getInt(4),
                        start = cursor.getInt(5),
                        end = cursor.getInt(6)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun readParaAll(): ArrayList<JuzModel> {
        val data =ArrayList<JuzModel>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM para"
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    JuzModel(
                        id = cursor.getInt(0),
                        name = cursor.getString(1),
                        ayatStart = cursor.getInt(3),
                        start = cursor.getInt(4),
                        end = cursor.getInt(5),
                        size = cursor.getInt(2)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun readSurahX(id: Int): Surah? {
        var data: Surah? = null
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM surah WHERE id == $id LIMIT 1"
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            data = Surah(
                id = cursor.getInt(0),
                name = cursor.getString(1),
                arabic = cursor.getString(2),
                revelation = cursor.getString(3),
                verse = cursor.getInt(4),
                start = cursor.getInt(5),
                end = cursor.getInt(6)
            )
        }
        cursor.close()
        return data
    }

    fun readParaX(id: Int): JuzModel? {
        var data: JuzModel? = null
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM para WHERE id == $id LIMIT 1"
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            data = JuzModel(
                id = cursor.getInt(0),
                name = cursor.getString(1),
                ayatStart = cursor.getInt(3),
                start = cursor.getInt(4),
                end = cursor.getInt(5),
                size = cursor.getInt(2)
            )
        }
        cursor.close()
        return data
    }
}