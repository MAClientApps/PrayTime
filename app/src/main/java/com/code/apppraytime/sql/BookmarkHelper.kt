package com.code.apppraytime.sql

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.code.apppraytime.BuildConfig

@SuppressLint("SdCardPath")
class BookmarkHelper(val context: Context) :
    SQLiteOpenHelper(context,
        "/data/data/${BuildConfig.APPLICATION_ID}/bookmark.db",
        null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            ("CREATE TABLE "
                    + "bookmarks"
                    ) + " (" +
                    "id INTEGER PRIMARY KEY, " +
                    "marked INTEGER)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("Upgrade", "Cannot update DB")
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        db?.disableWriteAheadLogging()
    }

    fun onReadAll(): ArrayList<Int> {
        val data = ArrayList<Int>()
        val cursor: Cursor = this.readableDatabase
            .rawQuery(
                "SELECT * FROM bookmarks", null
            )
        if (cursor.moveToFirst()) {
            do {
                data.add(cursor.getInt(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

     fun onRead(x: Int): Boolean {
         val cursor: Cursor = this.readableDatabase.rawQuery(
             "SELECT * FROM bookmarks WHERE id == $x LIMIT 1",
             null
         )
         return if (cursor.moveToFirst()) {
             cursor.close()
             true
         } else {
             cursor.close()
             false
         }
     }

    fun onWrite(x: Int) {
        this.writableDatabase.run {
            val contentValues = ContentValues()
            contentValues.put("id", x)
            contentValues.put("marked", 10)
            insert("bookmarks", null, contentValues)
            close()
        }
    }

    fun onDelete(x: Int) {
        val cursor: Cursor = this.readableDatabase.rawQuery(
            "DELETE FROM bookmarks WHERE id = $x",
            null
        )
        cursor.moveToNext()
        cursor.close()
    }
}