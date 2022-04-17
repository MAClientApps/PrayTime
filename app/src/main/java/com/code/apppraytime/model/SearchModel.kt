package com.code.apppraytime.model

import androidx.annotation.Keep

@Keep
data class SearchModel(
    val type: Int,
    val pos: Int,
    val name: String = "",
    val revelation: String = "",
    val verse: Int = 0,
    val nameAr: String = "",

    //
    val surah: Int = 0,
    val ayat: Int = 0,
    val arabic: String = "",
    val pronunciation: String = "",
    val translation: String = ""
)