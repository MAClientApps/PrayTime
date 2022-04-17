package com.code.apppraytime.model

import androidx.annotation.Keep

@Keep
data class Surah(
    val id: Int,
    val name: String,
    val arabic: String,
    val revelation: String,
    val verse: Int,
    val start: Int,
    val end: Int
)
