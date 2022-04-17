package com.code.apppraytime.sql

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.code.apppraytime.BuildConfig
import com.code.apppraytime.model.TranslationModel
import javax.inject.Singleton

@SuppressLint("SdCardPath")
@Singleton
class TranslationHelper(context: Context, name: String) :
    SQLiteOpenHelper(context,
        "/data/data/${BuildConfig.APPLICATION_ID}" +
                "/quran/$name.db", null, 1) {

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

    fun readAll(): ArrayList<TranslationModel> {
        val data = ArrayList<TranslationModel>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM translation"
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                data.add(TranslationModel(
                    id = cursor.getInt(0),
                    surah = cursor.getInt(1),
                    ayah = cursor.getInt(2),
                    text = cursor.getString(3)
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun readX2Y(start: Int, end: Int, size: Int): ArrayList<TranslationModel> {
        val data = ArrayList<TranslationModel>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM translation WHERE id BETWEEN $start AND $end LIMIT $size"
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                data.add(TranslationModel(
                    id = cursor.getInt(0),
                    surah = cursor.getInt(1),
                    ayah = cursor.getInt(2),
                    text = cursor.getString(3)
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun readAt(id: Int): TranslationModel? {
        var data: TranslationModel? = null
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM translation WHERE id == $id LIMIT 1"
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            data = TranslationModel(
                id = cursor.getInt(0),
                surah = cursor.getInt(1),
                ayah = cursor.getInt(2),
                text = cursor.getString(3)
            )
        }
        cursor.close()
        return data
    }
}