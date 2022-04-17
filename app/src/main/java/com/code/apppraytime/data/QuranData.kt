package com.code.apppraytime.data

import android.content.Context
import com.code.apppraytime.data.model.QAModel
import com.code.apppraytime.data.model.QPModel
import com.code.apppraytime.shared.Application
import com.code.apppraytime.sql.IndexHelper
import com.code.apppraytime.sql.TranslationHelper

class QuranData(context: Context) {

    private val indexHelper = IndexHelper(context)
    private val pronunciationDB = TranslationHelper(context, "latin")
    private val arabicDB = TranslationHelper(
        context, if (Application(context).arabic) "utsmani" else "indopak"
    )
    private val translationDB = TranslationHelper(context, Application(context).translation)

    fun getSurah(x: Int): ArrayList<QAModel> {
        val data = ArrayList<QAModel>()
        indexHelper.readSurahX(x)?.let {
            val proData = pronunciationDB.readX2Y(it.start, it.end, it.verse)
            val arabicData = arabicDB.readX2Y(it.start, it.end, it.verse)
            val transData = translationDB.readX2Y(it.start, it.end, it.verse)
            data.add(
                QAModel(
                    id = x,
                    ayat = it.verse,
                    arabic = it.arabic,
                    pronunciation = it.revelation,
                    translation = it.name
                )
            )
            for (i in 0 until it.verse) {
                data.add(
                    QAModel(
                        id = arabicData[i].id,
                        ayat = arabicData[i].ayah,
                        arabic = arabicData[i].text,
//                        arabic = tData[i],
                        pronunciation = proData[i].text,
                        translation = transData[i].text,
                    )
                )
            }
        }
        return data
    }

    fun getPara(x: Int): ArrayList<QPModel> {
        val data = ArrayList<QPModel>()
        indexHelper.readParaX(x)?.let {
            var runningSurah = 0
            var runningSurahName = ""
            val proData = pronunciationDB.readX2Y(it.start, it.end, it.size)
            val arabicData = arabicDB.readX2Y(it.start, it.end, it.size)
            val transData = translationDB.readX2Y(it.start, it.end, it.size)

            for (i in 0 until it.size) {
                if (runningSurah != arabicData[i].surah) {
                    indexHelper.readSurahX(pronunciationDB.readAt(it.start+i)!!.surah)!!.let { s ->
                        runningSurah = s.id
                        runningSurahName = s.name
                        if (arabicData[i].ayah==1) {
                            data.add(
                                QPModel(
                                    id = 0,
                                    surah = s.id,
                                    ayat = s.verse,
                                    name = runningSurahName,
                                    arabic = s.arabic,
                                    pronunciation = s.revelation,
                                    translation = s.name
                                )
                            )
                        }
                    }
                }
                data.add(
                    QPModel(
                        id = arabicData[i].id,
                        ayat = arabicData[i].ayah,
                        name = runningSurahName,
                        surah = runningSurah,
                        arabic = arabicData[i].text,
                        pronunciation = proData[i].text,
                        translation = transData[i].text,
                    )
                )
            }
        }
        return data
    }

    fun getDataOfID(x: Int): QPModel {
        val proData = pronunciationDB.readAt(x)!!
        val arabicData = arabicDB.readAt(x)!!
        val transData = translationDB.readAt(x)!!

        val name = indexHelper.readSurahX(arabicData.surah)!!.name

        return QPModel(
            id = arabicData.id,
            surah = arabicData.surah,
            ayat = arabicData.ayah,
            name = name,
            arabic = arabicData.text,
            pronunciation = proData.text,
            translation = transData.text
        )
    }
}