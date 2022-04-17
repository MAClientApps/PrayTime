package com.code.apppraytime.data.model

import androidx.annotation.Keep

@Keep
data class QPModel(
    val id: Int,
    val ayat: Int,
    val surah: Int,
    val name: String,
    val arabic: String,
    val pronunciation: String,
    val translation: String,
)