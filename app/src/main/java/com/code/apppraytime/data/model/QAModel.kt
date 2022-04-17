package com.code.apppraytime.data.model

import androidx.annotation.Keep

@Keep
data class QAModel(
    val id: Int,
    val ayat: Int,
    val arabic: String,
    val pronunciation: String,
    val translation: String
)